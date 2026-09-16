pipeline {

    agent {
        label 'linux'
    }

    parameters {
    choice(
        name: 'ENVIRONMENT',
        choices: ['dev', 'staging', 'prod'],
        description: 'Select the deployment environment'
    )
}

    stages {


        stage('Show Parameters') {
            steps {
                echo "Selected environment: ${params.ENVIRONMENT}"
            }
        }
       
        stage('checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build') {
            steps {
                echo "Building application for ${params.ENVIRONMENT} environment..."

                sh '''
                    docker run --rm -v "$WORKSPACE:/app" -w /app maven:3.9-eclipse-temurin-21 mvn clean package

                '''
            }
        }
        stage('Test') {
            steps {
                echo 'Running maven tests...'
                sh '''
                   docker run --rm -v "$WORKSPACE:/app" -w /app maven:3.9-eclipse-temurin-21 mvn test
                '''
            }
        }
    }
    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Please check the logs for details.'
        }
    }



}