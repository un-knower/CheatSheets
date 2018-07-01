https://github.com/effectivejenkins/effective_jenkins_volume2/blob/master/jenkins_machines/Vagrantfile
https://github.com/effectivejenkins/effective_jenkins_volume2




vagrant init bento/ubuntu-16.04

vi Vagrantfile
# remove all comments, add line:

config.vm.network "forwarded_port", guest:8080, host:8080


vagrant up




  config.vm.define "jenkins_node1" do |jenkins_node1|
	  jenkins_node1.vm.network "private_network", ip: "192.168.50.2"
  end

$ vagrant up jenkins_node1





$ vagrant ssh jenkins_master
sudo su jenkins


$ vagrant ssh jenkins_node1
sudo adduer --gecos "" jenkins
# add user to sudoers
sudo usermod -aG sudo jenkins

# create ssh-keygen
# copy to another node
ssh-copy-id jenkins@192.168.50.2

# update
sudo apt-get update

# install java
sudo apt-get install -y default-jre





JENKINS plugin   - manage plugins, available
1. Pipeline
2. Pipeline Stage View
3. Parametrized Trigger  (pipeline, dependency)
4. Build Pipeline View, Delivery Pipeline Build
5. Build Monitor
# we need to specify predefined parameters, so e.g. workspace can be reused:
# we specify that in initial job  mvn clean compile as POST build steps
SPRING_WORKSPACE=${WORKSPACE}

# then in 2nd step, TEST job we select, that THIS PROJECT IS PARAMETRIZED, and specify STRING PARAMETER   :  SPRING_WORKSPACE
# also click "Advanced", and select  USE CUSTOM WORKSPACE, and type there  ${SPRING_WORKSPACE}
# then go to BUILD, and choose MAVEN TOP-LEVEL and type  test
# then POST-BILD, click trigger parameters, choose final project, and also select CURRENT BUILD PARAMETERS, which preserves parameters

# in 3rd step, also choose THIS PROJECT IS PARAMETRIZED, name: SPRING_WORKSPACE
# USE CUSTOM WORK SPACE, ${SPRING_WORKSPACE}

WE CAN ALSO SET UP AUTO-TRIGGER OF FIRST STEP (compile) WHEN THERE IS COMMIT TO GITHUB
1. go to first job , configure, and select  BUILD TRIGGERS --> POLL SCM, type  H/5 * * * *   to check every 5 minutes


Pipeline

node {
	stage('echo') {
		if (isUnix()) {
			sh 'ls -ltr'			
		} else {
			bat 'dir'
		}

		sh '''
			./say_hello.sh
			ls -ltr
			'''
	}
	stage('checkout') {
		git 'https://github.com/..../project.git'
	}
	stage('use passwords') {
		withCredentials( [
			usernameColonPassword(credentialsId: 'my_id_1', 
			variable: 'MY_CREDENTIALS_1')]) {
				echo "My password is '${MY_CREDENTIALS_1}' "
			}
	}
}



/*
If you are using Gmail:
Jenkins Dashboard > Manage Jenkins > Configure System
Section: E-mail Notification
SMTP server: smtp.gmail.com
Use SMTP Authentication: yes
User name: Your full Gmail address (e.g. example@gmail.com)
Password: Your Gmail password
SMTP port: 465
Use SSL required: yes
*/
node {
  try {
    stage('echo') {
      sh 'echo "Hello World"'
      def obj = null
      sh "${obj.class}"
    }
  } catch (NullPointerException e) {
    error 'broken pipeline - null pointer exception'
    currentBuild.result = 'FAILURE'
    //currentBuild.result = 'UNSTABLE'
  } finally {
    stage('Send notification') {
      echo "this will always run"
      mail to: 'effectivejenkins@gmail.com',
           body: "Something is wrong with ${env.BUILD_URL}",
           subject: "Failed Pipeline: ${currentBuild.fullDisplayName}"
    }
  }
}



DECLARATIVE JENKINSFILE
common directives:
- triggers , automatically triggers pipeline based on definition
- parameters , defines one or more param, which user should provide
- tools ,   defines tools configured on jenkins
- stage,   steps/agents
- when , give control on what should be evecuted on pipeline

pipeline {
    agent { label 'linux' }
    agent any
    agent none  // any available, uzywamy jak definiujemty pozniej na kazdy stage osobno
    agent { docker 'maven:3.5-alpine' }
    tools {
        maven 'M3'
    }
    parameters {
    	string(name: 'VERSION', defaultValue: '1.0.0', description: 'what is the version to build?')
    }
    stages {
        stage ('Checkout') {
          steps {
            git 'https://github.com/effectivejenkins/myProject.git'
          }
        }
        stage('Build') {
            steps {
                sh 'mvn clean compile'
                sh "./build.sh ${params.VERSION}"
            }
        }
        stage('another') {
        	echo "xxx"
        	script {
        		for (int i=0; i<5 ; ++i) {
        			echo "number ${i}"
        		}
        	}
        }
        stage('example build using docker container') {
        	agent { docker 'maven:3.5-alpine'}		// required, because on that docker container, there might be no 'scp' 
        	agent { docker 'openjdk:8-jre'}
            steps {
                sh 'mvn clean package'
                junit '**/target/surefire-reports/TEST-*.xml'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
        stage("DEPLOY - AKS FIRST") {
        	steps {
        		input "Do you approve deployment?"
        		echo 'Deploying...'
        		sh 'scp target/*.jar jenkins@192.168.50.10:/opt/pet/'
        		sh "ssh jenkins@192.168.50.10 'nohup java -jar /opt/pet/spring-petclinic-1.5.1.jar &'"
        	}

        }
    }
}