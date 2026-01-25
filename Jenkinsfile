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
				cleanWorkSpace()
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
	}
}