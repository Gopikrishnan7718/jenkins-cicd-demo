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

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh '''
                        docker run --rm \
                        -v "$WORKSPACE:/app" \
                        -w /app \
                        -e SONAR_HOST_URL="$SONAR_HOST_URL" \
                        -e SONAR_TOKEN="$SONAR_AUTH_TOKEN" \
                        maven:3.9-eclipse-temurin-21 \
                        mvn sonar:sonar
                    '''
                }
            }
        }

        stage('Quality Gate') {
        steps {
            timeout(time: 5, unit: 'MINUTES') {
                waitForQualityGate abortPipeline: true
            }
          }
        }
        
        stage ('docker build') {
            steps {
                echo "Building Docker image for ${params.ENVIRONMENT} environment..."
                sh '''
                    docker build -t jenkins-cicd-demo:${BUILD_NUMBER} .
                '''
            }
        }
        
        stage('Trivy Scan') {
            steps {
                echo "Scanning Docker image for vulnerabilities..."

               sh '''
                    docker run --rm \
                    -v /var/run/docker.sock:/var/run/docker.sock \
                    -v trivy-cache:/root/.cache/trivy \
                    aquasec/trivy:latest \
                    image \
                    --severity HIGH,CRITICAL \
                    --exit-code 1 \
                    jenkins-cicd-demo:${BUILD_NUMBER}
                '''
            }
        }

        stage('Push to ECR') {
            steps {
                echo "Pushing Docker image to Amazon ECR..."

                sh '''
                    AWS_REGION="ap-south-1"
                    AWS_ACCOUNT_ID=$(aws sts get-caller-identity \
                        --query Account \
                        --output text)

                    ECR_REGISTRY="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
                    ECR_REPOSITORY="${ECR_REGISTRY}/jenkins-cicd-demo"
                    IMAGE_TAG="${BUILD_NUMBER}"

                    echo "Authenticating Docker with Amazon ECR..."

                    aws ecr get-login-password \
                        --region "${AWS_REGION}" | \
                        docker login \
                        --username AWS \
                        --password-stdin "${ECR_REGISTRY}"

                    echo "Tagging Docker image..."

                    docker tag \
                        "jenkins-cicd-demo:${IMAGE_TAG}" \
                        "${ECR_REPOSITORY}:${IMAGE_TAG}"

                    echo "Pushing Docker image..."

                    docker push \
                        "${ECR_REPOSITORY}:${IMAGE_TAG}"
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