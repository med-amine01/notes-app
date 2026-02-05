pipeline {
    agent {
        label 'jenkins-agent'
    }

    tools {
        jdk 'Java21'
        maven 'Maven3'
    }

    environment {
        APP_NAME = 'notes-app'
        RELEASE_VERSION = '1.0.0'

        DOCKER_USERNAME = 'medaminechebbi'
        DOCKER_CREDENTIALS_ID = 'dockerhub-token'

        IMAGE_NAME = "${DOCKER_USERNAME}/${APP_NAME}"
        IMAGE_TAG  = "${RELEASE_VERSION}-${BUILD_NUMBER}"
    }

    stages {

        stage("Clean workspace") {
            steps {
                cleanWs()
            }
        }

        stage("Checkout from SCM") {
            steps {
                git branch: 'main',
                    credentialsId: 'github',
                    url: 'https://github.com/med-amine01/notes-app.git'
            }
        }

        stage("Build application") {
            steps {
                sh "mvn clean package"
            }
        }

        stage("Test application") {
            steps {
                sh "mvn test"
            }
        }

        stage("SonarQube analysis") {
            steps {
                withSonarQubeEnv(credentialsId: 'jenkins-sonarqube-token') {
                    sh "mvn sonar:sonar"
                }
            }
        }

        stage("Quality Gate") {
            steps {
                waitForQualityGate abortPipeline: false,
                    credentialsId: 'jenkins-sonarqube-token'
            }
        }

        stage("Build & Push Docker image") {
            steps {
                script {
                    docker.withRegistry('', DOCKER_CREDENTIALS_ID) {
                        def dockerImage = docker.build("${IMAGE_NAME}")
                        dockerImage.push("${IMAGE_TAG}")
                        dockerImage.push("latest")
                    }
                }
            }
        }

        stage("Trivy Image Scan") {
            steps {
                sh """
                  trivy image \
                    --severity HIGH,CRITICAL \
                    --exit-code 1 \
                    --no-progress \
                    ${IMAGE_NAME}:${IMAGE_TAG}
                """
            }
        }


		stage("Cleanup Artifacts") {
			steps {
				script {
					sh """
					docker rmi ${IMAGE_NAME}:${IMAGE_TAG}
					docker rmi ${IMAGE_NAME}:latest
					docker image prune -f
					"""
				}
			}
		}
    }
}