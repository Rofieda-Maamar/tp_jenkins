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
    }
}
