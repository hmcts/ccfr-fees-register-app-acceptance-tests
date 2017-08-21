#!groovy
@Library("Reform")
import uk.gov.hmcts.Ansible
import uk.gov.hmcts.Packager
import uk.gov.hmcts.RPMTagger

def packager = new Packager(this, 'cc')
def ansible = new Ansible(this, 'ccfr')
def rtMaven = Artifactory.newMavenBuild()
RPMTagger rpmTagger = new RPMTagger(this, 'fees-register-api', packager.rpmName('fees-register-api', params.rpmVersion), 'cc-local')

properties([
        [$class: 'GithubProjectProperty', displayName: 'Fees Register API database acceptance tests', projectUrlStr: 'https://git.reform.hmcts.net/fees-register/fees-register-app-database-acceptance-tests'],
        parameters([string(defaultValue: '', description: 'RPM Version', name: 'rpmVersion')])
])

lock('Fees Register API database acceptance tests') {
    node {
        try {
            def deploymentRequired = !params.rpmVersion.isEmpty()
            def version = "{fees_register_api_version: ${params.rpmVersion}}"

            if (deploymentRequired) {
                stage('Deploy to Dev') {
                    ansible.runDeployPlaybook(version, 'dev')
                    rpmTagger.tagDeploymentSuccessfulOn('dev')
                }
            }

            stage('Run acceptance tests') {
                deleteDir()
                checkout scm
                rtMaven.tool = 'apache-maven-3.3.9'
                rtMaven.run pom: 'pom.xml', goals: 'clean package surefire-report:report -Dspring.profiles.active=devA -Dtest=**/acceptancetests/*Test'

                publishHTML([
                        allowMissing         : false,
                        alwaysLinkToLastBuild: true,
                        keepAll              : false,
                        reportDir            : 'target/site',
                        reportFiles          : 'surefire-report.html',
                        reportName           : 'Acceptance Test Report (dev)'
                ])
            }

            if (deploymentRequired) {
                stage('Tag testing passed') {
                    rpmTagger.tagTestingPassedOn('dev')
                }

                stage('Deploy to Test') {
                    ansible.runDeployPlaybook(version, 'test')
                    rpmTagger.tagDeploymentSuccessfulOn('test')
                }

                stage('Run acceptance tests') {
                    deleteDir()
                    checkout scm
                    rtMaven.run pom: 'pom.xml', goals: 'clean package surefire-report:report -Dspring.profiles.active=devB -Dtest=**/acceptancetests/*Test'

                    publishHTML([
                            allowMissing         : false,
                            alwaysLinkToLastBuild: true,
                            keepAll              : false,
                            reportDir            : 'target/site',
                            reportFiles          : 'surefire-report.html',
                            reportName           : 'Acceptance Test Report (test)'
                    ])

                    rpmTagger.tagTestingPassedOn('test')
                }
            }
        } catch (err) {
            notifyBuildFailure channel: '#cc-payments-tech'
            throw err
        }
    }
}