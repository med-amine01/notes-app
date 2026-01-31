pipeline {
	agent {
		label 'jenkins-agent'
	}
	tools {
		jdk 'Java21'
		maven 'Maven3'
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
				script {
					withSonarQubeEnv(credentialsId: 'jenkins-sonarqube-token') {
						sh "mvn sonar:sonar"
					}
				}
			}
		}
		stage("Quality Gate") {
			steps {
				script {
					waitForQualityGate abortPipeline: false, credentialsId: 'jenkins-sonarqube-token'
				}
			}
		}
	}
}