#!groovy
@Library("Reform")
def rtMaven = Artifactory.newMavenBuild()

properties([
        [$class: 'GithubProjectProperty', displayName: 'Fees Register API database acceptance tests', projectUrlStr: 'https://github.com/hmcts/ccfr-fees-register-app-acceptance-tests'],
        parameters([
                string(defaultValue: 'latest', description: 'fees-api Docker Version', name: 'feesApiDockerVersion'),
                string(defaultValue: 'latest', description: 'fees-database Docker Version', name: 'feesDatabaseDockerVersion')
        ])
])

lock('Fees Register API database acceptance tests') {
    node {
        try {
            stage('Checkout') {
                deleteDir()
                checkout scm
            }

            try {
                stage('Start Docker Images') {
                    env.FEES_API_DOCKER_VERSION = params.feesApiDockerVersion
                    env.FEES_DATABASE_DOCKER_VERSION = params.feesDatabaseDockerVersion

                    sh 'docker-compose pull'
                    sh 'docker-compose up -d fees-api'
                    sh 'docker-compose up wait-for-startup'
                }

                stage('Run acceptance tests') {
                    rtMaven.tool = 'apache-maven-3.3.9'
                    rtMaven.run pom: 'pom.xml', goals: 'clean package surefire-report:report -Dspring.profiles.active=docker -Dtest=**/acceptancetests/*Test'

                    publishHTML([
                            allowMissing         : false,
                            alwaysLinkToLastBuild: true,
                            keepAll              : false,
                            reportDir            : 'target/site',
                            reportFiles          : 'surefire-report.html',
                            reportName           : 'Acceptance Test Report'
                    ])
                }
            } finally {
                stage('Stop Docker Images') {
                    sh 'docker-compose down'
                }
            }
        } catch (err) {
            notifyBuildFailure channel: '#cc-payments-tech'
            throw err
        }
    }
}
