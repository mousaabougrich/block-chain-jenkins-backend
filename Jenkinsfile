pipeline {
    agent any

    triggers {
        pollSCM('H/5 * * * *')  // Check for changes every 5 minutes
    }

    tools {
        maven 'Maven'
        jdk 'JDK'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/mousaabougrich/pfa-jenkins-backend.git'
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean test jacoco:report'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    // Replace with your actual SonarQube IP
                    def sonarIP = '172.25.0.5'
                    sh """
                        mvn sonar:sonar \
                        -Dsonar.projectKey=biochain \
                        -Dsonar.projectName=biochain \
                        -Dsonar.host.url=http://${sonarIP}:9000 \
                        -Dsonar.sources=src/main/java \
                        -Dsonar.tests=src/test/java \
                        -Dsonar.java.binaries=target/classes
                    """
                }
            }
        }
    }

    post {
        success {
            echo 'Build and SonarQube analysis completed successfully!'
        }
        failure {
            echo 'Build failed!'
        }
    }
}
