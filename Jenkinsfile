pipeline {
    agent any

    tools {
        jdk 'JDK17'
    }

    stages {
        stage('Build') {
            steps {
                bat './gradlew build'
            }
        }
    }
}
