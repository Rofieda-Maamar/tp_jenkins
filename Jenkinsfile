pipeline {
    agent any
    tools {
        jdk 'JDK-21'
    }

    environment {
        MAVEN_URL = 'https://mymavenrepo.com/repo/cEmjfkxugPlzLxXg1A2B/'
        PROJECT_NAME = 'TP7-API-INTEGRATION' // ou le nom exact de ton projet Gradle
        PROJECT_VERSION = '1.0-SNAPSHOT'
    }

    stages {
        // ============================================
        // PHASE 1: TEST
        // ============================================
        stage('Test') {
            steps {
                echo '========== Phase Test =========='

                echo 'Execution des tests unitaires...'
                bat 'gradlew clean test'

                echo 'Archivage des resultats de tests...'
                junit '**/build/test-results/test/*.xml'

                echo 'Generation des rapports Cucumber...'
                bat 'gradlew generateCucumberReports'
                cucumber buildStatus: 'UNSTABLE',
                         reportTitle: 'Rapport Cucumber',
                         fileIncludePattern: '**/*.json',
                         jsonReportDirectory: 'reports'
            }
        }

        // ============================================
        // PHASE 2: CODE ANALYSIS (SonarQube)
        // ============================================
        stage('Code Analysis') {
            steps {
                echo '========== Phase Code Analysis =========='
                echo 'Analyse du code avec SonarQube...'

                withSonarQubeEnv('SonarQube') { // "SonarQube" is the name of the Sonar server in Jenkins
                    withCredentials([string(credentialsId: 'SONAR_AUTH_TOKEN', variable: 'SONAR_AUTH_TOKEN')]) {
                        bat "gradlew compileJava sonar -Dsonar.login=%SONAR_AUTH_TOKEN%"
                    }
                }
            }
        }

        // ============================================
        // PHASE 3: CODE QUALITY (Quality Gate)
        // ============================================
        /*stage('Code Quality') {
            steps {
                echo '========== Phase Code Quality =========='
                echo 'Verification du Quality Gate...'

                timeout(time: 10, unit: 'MINUTES') {
                    script {
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            error "Pipeline aborted due to quality gate failure: ${qg.status}"
                        }
                    }
                }
            }
        }*/

        // ============================================
        // PHASE 4: BUILD
        // ============================================
        stage('Build') {
            steps {
                echo '========== Phase Build =========='

                echo 'Generation du fichier JAR...'
                bat 'gradlew build -x test'

                echo 'Generation de la Javadoc...'
                bat 'gradlew generateJavadoc'

                echo 'Archivage des artefacts...'
                archiveArtifacts artifacts: '**/build/libs/*.jar',
                                 fingerprint: true,
                                 allowEmptyArchive: false

                archiveArtifacts artifacts: '**/build/docs/javadoc/**/*',
                                 fingerprint: true,
                                 allowEmptyArchive: true

                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'build/docs/javadoc',
                    reportFiles: 'index.html',
                    reportName: 'Javadoc',
                    reportTitles: 'Documentation Javadoc'
                ])
            }
        }

        // ============================================
        // PHASE 5: DEPLOY (MyMavenRepo)
        // ============================================
        stage('Deploy') {
            steps {
                echo '========== Phase Deploy =========='
                withCredentials([usernamePassword(credentialsId: 'maven-repo', usernameVariable: 'MAVEN_USERNAME', passwordVariable: 'MAVEN_PASSWORD')]) {
                    bat """
                        gradlew publish ^
                          -Pmaven.url=%MAVEN_URL% ^
                          -Pmaven.username=%MAVEN_USERNAME% ^
                          -Pmaven.password=%MAVEN_PASSWORD%
                    """
                }
                echo "Deploiement reussi sur ${env.MAVEN_URL}"
            }
        }

        // ============================================
        // PHASE 6: NOTIFICATION (Slack)
        // ============================================
        // ============================================
        // PHASE 6: NOTIFICATION (Slack)
        // ============================================
        stage('Notification') {
            steps {
                echo '========== Phase Notification =========='
                script {
                    def buildStatus = currentBuild.result ?: 'SUCCESS'
                    def color = buildStatus == 'SUCCESS' ? 'good' : 'danger'
                    def emoji = buildStatus == 'SUCCESS' ? '✅' : '❌'
                    def statusText = buildStatus == 'SUCCESS' ? 'réussi' : 'échoué'

                    def payload = """
                    {
                        "channel": "#tous-ogl",
                        "username": "Jenkins",
                        "text": "${emoji} Build ${statusText}",
                        "attachments": [
                            {
                                "color": "${color}",
                                "fields": [
                                    {
                                        "title": "Projet",
                                        "value": "${env.PROJECT_NAME}",
                                        "short": true
                                    },
                                    {
                                        "title": "Version",
                                        "value": "${env.PROJECT_VERSION}",
                                        "short": true
                                    },
                                    {
                                        "title": "Build",
                                        "value": "#${env.BUILD_NUMBER}",
                                        "short": true
                                    },
                                    {
                                        "title": "Status",
                                        "value": "${buildStatus}",
                                        "short": true
                                    },
                                    {
                                        "title": "Job",
                                        "value": "${env.JOB_NAME}",
                                        "short": false
                                    },
                                    {
                                        "title": "URL",
                                        "value": "${env.BUILD_URL}",
                                        "short": false
                                    }
                                ]
                            }
                        ]
                    }
                    """

                    withCredentials([string(credentialsId: 'SLACK_AUTH_TOKEN', variable: 'WEBHOOK_URL')]) {
                        bat """
                            curl -X POST -H "Content-type: application/json" --data "${payload.replaceAll('\n', '').replaceAll('"', '\\"')}" %WEBHOOK_URL%
                        """
                    }
                }
            }
        }


}
}
