def call(String name = 'human') {
node {
 stage ('Docker build') {
 sh "ls -l jenkins-project; if [ \$? -eq 0 ]; then rm -rf jenkins-project; git clone https://github.com/mireillef/jenkins-project.git ; else git clone https://github.com/mireillef/jenkins-project.git; fi" 
 sh "docker build -t mireille/jenkins-app jenkins-project/ "
 }
 
 stage ('Docker push') {
 sh "\$(aws ecr get-login --no-include-email --region us-east-1)"     
 sh "docker tag mireille/jenkins-app 589933236526.dkr.ecr.us-east-1.amazonaws.com/jenkins-repo:${env.BUILD_NUMBER}"
 sh "docker push 589933236526.dkr.ecr.us-east-1.amazonaws.com/jenkins-repo:${env.BUILD_NUMBER}"
 }
 
 stage ('Docker pull') {
 sh "docker pull 589933236526.dkr.ecr.us-east-1.amazonaws.com/jenkins-repo:latest"
 }
 
 stage ('Docker run') {
 sh "check=`docker ps -q  --filter ancestor=mireille/jenkins-project| wc -l` ; if [ \$check -gt 0 ]; then docker stop \$(docker ps -q  --filter ancestor=mireille/jenkins-project); fi"     
 sh "docker tag 589933236526.dkr.ecr.us-east-1.amazonaws.com/jenkins-repo:latest mireille/jenkins-project:latest"
 sh "docker run -itd -p 8888:8080 mireille/jenkins-project"
 }
 
 stage ('Test image') {
  try {
            sh "wget http://localhost:8888/directeam -O /dev/null"
            return true
        } catch (Exception e) {
            throw e;
    }
    }
 stage ('Send message') {
 NotifySuccess()
    }
    }
    }
