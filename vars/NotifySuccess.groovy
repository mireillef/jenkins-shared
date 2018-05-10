 def call (String name = 'human') {
     slackSend (color: '#FFFF00', message: "Build: ${env.JOB_NAME} Number: [${env.BUILD_NUMBER}] has succeeded.")
     }

