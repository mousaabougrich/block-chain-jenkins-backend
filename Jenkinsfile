pipeline {
    agent any

    triggers {
        pollSCM('H/5 * * * *')
    }

    environment {
        SONAR_IP = '172.25.0.5'  // Changez avec votre IP
        MAVEN_HOME = '/usr/share/maven'
        PATH = "${MAVEN_HOME}/bin:${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                echo '📥 Cloning repository...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo '🔨 Building project...'
                sh 'mvn clean compile'
            }
        }

        stage('Unit Tests') {
            steps {
                echo '🧪 Running tests...'
                sh 'mvn test'
            }
        }

        stage('Code Coverage') {
            steps {
                echo '📊 Generating coverage report...'
                sh 'mvn jacoco:report'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo '🔍 Running SonarQube analysis...'
                sh """
                    mvn sonar:sonar \
                    -Dsonar.projectKey=biochain \
                    -Dsonar.projectName=biochain \
                    -Dsonar.host.url=http://${SONAR_IP}:9000 \
                    -Dsonar.sources=src/main/java \
                    -Dsonar.tests=src/test/java \
                    -Dsonar.java.binaries=target/classes \
                    -Dsonar.java.test.binaries=target/test-classes
                """
            }
        }
    }

    post {
        success {
            echo '✅ Build completed successfully!'
            echo "📊 View SonarQube: http://${SONAR_IP}:9000/dashboard?id=biochain"
        }
        failure {
            echo '❌ Build failed!'
        }
    }
}
