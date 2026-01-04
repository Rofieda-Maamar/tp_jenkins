pipeline {
    agent any

    tools { jdk 'JDK-21' }

    stages {
        stage('Build') {
            steps {
                bat """
                    .\\gradlew.bat clean build --no-daemon ^
                    -Pmaven.url=%url% ^
                    -Pmaven.username=%username% ^
                    -Pmaven.password=%password%
                """
            }
        }

        stage('Deploy') {
            steps {
                bat """
                    .\\gradlew.bat publish --no-daemon ^
                    -Pmaven.url=%url% ^
                    -Pmaven.username=%username% ^
                    -Pmaven.password=%password%
                """
            }
        }

        stage('Test') {
            steps {
                echo 'Running Unit Tests and Cucumber Tests'

                // Run unit tests and Cucumber tests
                sh './gradlew clean test cucumber'

                // Archive JUnit XML results (unit tests)
                junit '**/build/test-results/**/*.xml'

                // Publish HTML reports for unit tests
                publishHTML(target: [
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'build/reports/tests/test',
                    reportFiles: 'index.html',
                    reportName: 'Unit Test Report'
                ])

                // Publish HTML reports for Cucumber
                publishHTML(target: [
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'build/reports/tests/cucumber',
                    reportFiles: 'index.html',
                    reportName: 'Cucumber Report'
                ])
            }
        }

    }
}
