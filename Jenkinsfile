pipeline {
    agent any
    tools {
        jdk 'JDK-21'
    }

    environment {
        MAVEN_REPO_URL = "${env.MAVEN_REPO_URL}"
        SONAR_HOST_URL = "${env.SONAR_HOST_URL}"
        PROJECT_NAME = "TP7-API-INTEGRATION"
        PROJECT_VERSION = "1.0-SNAPSHOT"
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
        // PHASE 4: BUILD
        // ============================================
        stage('Build') {
            steps {
                echo 'Generating JAR and Javadoc'
                bat './gradlew clean build javadoc'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
                    archiveArtifacts artifacts: 'build/docs/javadoc/**', fingerprint: true
                }
            }
        }

        // ============================================
        // PHASE 5: DEPLOY
        // ============================================
        stage('Deploy') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'maven-repo',
                    usernameVariable: 'username',
                    passwordVariable: 'password'
                )]) {
                    bat """
                        ./gradlew publish ^
                        -Pmaven.url=%MAVEN_REPO_URL% ^
                        -Pmaven.username=%username% ^
                        -Pmaven.password=%password%
                    """
                }
            }
        }

        // ============================================
        // PHASE 6: NOTIFICATION
        // ============================================
        stage('Notification') {
            steps {
                echo '========== Phase Notification =========='
                script {
                    // Email and Slack notifications
                    // ... (keep your original code here)
                }
            }
        }
    }

    // ============================================
    // POST ACTIONS
    // ============================================
    post {
        failure {
            echo '========== Build Failed =========='
            // Email and Slack notifications
        }
        success {
            echo '========== Build Successful =========='
        }
        always {
            echo '========== Pipeline termine =========='
            script {
                if (currentBuild.result == 'SUCCESS') {
                    cleanWs(deleteDirs: true, patterns: [[pattern: 'build/**', type: 'INCLUDE']])
                }
            }
        }
    }
}
