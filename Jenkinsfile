pipeline {
    agent any

    triggers {
        pollSCM('H/5 * * * *')
    }

    environment {
        SONAR_IP = '172.25.0.5'  // Votre IP
        MAVEN_OPTS = '-Xmx1024m'
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
                sh 'mvn clean compile -DskipTests'
            }
        }

        stage('Unit Tests') {
            steps {
                echo '🧪 Running tests with H2 database...'
                sh 'mvn test -Dspring.profiles.active=test'
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
                    -Dsonar.java.test.binaries=target/test-classes \
                    -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                """
            }
        }
    }

    post {
        success {
            echo '✅ Build completed successfully!'
            echo "📊 SonarQube: http://${SONAR_IP}:9000/dashboard?id=biochain"
        }
        failure {
            echo '❌ Build failed!'
        }
        always {
            junit '**/target/surefire-reports/*.xml'
            jacoco(execPattern: '**/target/jacoco.exec')
        }
    }
}
