pipeline {
    agent any

    tools {
            jdk 'JDK-21'  // Make sure this JDK is configured in Jenkins
        }

    stages {
        stage('Build') {
            steps {
                bat './gradlew build'
            }
        }
    }
}
