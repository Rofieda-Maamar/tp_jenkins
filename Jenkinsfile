pipeline {
    agent any

    tools {
        jdk 'JDK-21'
        gradle 'Gradle-8'
    }

    environment {
        MAVEN_URL = 'https://mymavenrepo.com/repo/cEmjfkxugPlzLxXg1A2B/'
    }

    stages {

        // =========================
        // 2.1 PHASE TEST

        // =========================
        // 2.2 PHASE CODE ANALYSIS
        // =========================
        stage('Code Analysis - SonarQube') {
            steps {
                withSonarQubeEnv('SonarQube') {
                     withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                        bat """
                            ./gradlew sonarqube ^
                            -Dsonar.projectKey=projet_main ^
                            -Dsonar.projectName=projet_main ^
                            -Dsonar.login=%SONAR_TOKEN%
                        """
                     }
                }
            }
        }

        // =========================
        // 2.3 PHASE CODE QUALITY
        // =========================
        stage('Quality Gate phase') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        // =========================
        // 2.4 PHASE BUILD
        // =========================
        stage('Build') {
            steps {
                echo 'Generating JAR and Javadoc'
                bat './gradlew clean build javadoc'
            }
            post {
                success {
                    // Archivage du JAR
                    archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true

                    // Archivage de la documentation
                    archiveArtifacts artifacts: 'build/docs/javadoc/**', fingerprint: true
                }
            }
        }

        // =========================
        // 2.5 PHASE DEPLOY
        // =========================
        stage('Deploy') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'maven-repo',
                    usernameVariable: 'username',
                    passwordVariable: 'password'
                )]) {
                    bat """
                        ./gradlew publish ^
                        -Pmaven.url=%MAVEN_URL% ^
                        -Pmaven.username=%username% ^
                        -Pmaven.password=%password%
                    """
                }
            }
        }
    }

    // =========================
    // 2.6 PHASE NOTIFICATION
    // =========================
    post {

        success {
            mail to: 'dev-team@example.com',
                 subject: "SUCCESS: Pipeline ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "Deployment successful.\nBuild URL: ${env.BUILD_URL}"

            slackSend(
                webhookUrl: credentials('slack-webhook'),
                message: "✅ Pipeline *${env.JOB_NAME}* #${env.BUILD_NUMBER} deployed successfully!"
            )
        }

        failure {
            mail to: 'dev-team@example.com',
                 subject: "FAILED: Pipeline ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "Pipeline failed.\nCheck logs: ${env.BUILD_URL}"

            slackSend(
                webhookUrl: credentials('slack-webhook'),
                message: "❌ Pipeline *${env.JOB_NAME}* #${env.BUILD_NUMBER} FAILED!"
            )
        }
    }
}
