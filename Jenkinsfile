pipeline {

    agent {
        label 'linux'
    }

    stages {
       
        stage('checkout') {
            steps {
                checkout scm
            }
        }
        stage('build') {
            steps {
                echo 'Building application...'
                sh '''
                    chmod +x app.sh
                    ./app.sh
                '''
            }
        }
        stage('Test') {
            steps {
                echo 'Running tests...'
                sh '''
                    ./app.sh | grep "THIS_TEXT_DOES_NOT_EXIST"
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