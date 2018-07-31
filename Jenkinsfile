#!/usr/bin/env groovy

node {
  checkout scm

  def server = Artifactory.server('artifactory')

  def artifactoryMaven = Artifactory.newMavenBuild()

  artifactoryMaven.tool = 'M3' // Tool name from Jenkins configuration
  artifactoryMaven.deployer releaseRepo:'libs-release-local', snapshotRepo:'libs-snapshot-local', server: server
  artifactoryMaven.resolver releaseRepo:'libs-release', snapshotRepo:'libs-snapshot', server: server

  def buildInfo = Artifactory.newBuildInfo()

  pom = readMavenPom file: 'pom.xml'

  env.POM_VERSION = "${pom.version}"

  sh "sed -i \"s|snapshot_version|${pom.version}|g\" hieradata/common.yaml"

  stage('Package') {
    // Run Maven:
    def buildInfoInstall = artifactoryMaven.run pom: 'pom.xml', goals: 'clean package checkstyle:checkstyle findbugs:findbugs cobertura:cobertura pmd:pmd install'

    buildInfo.append(buildInfoInstall)

    step([$class: 'hudson.plugins.checkstyle.CheckStylePublisher', pattern: '**/target/checkstyle-result.xml', unstableTotalAll:'0',unhealthy:'100', healthy:'100'])
    step([$class: 'PmdPublisher', pattern: '**/target/pmd.xml'])
    step([$class: 'FindBugsPublisher', pattern: '**/findbugsXml.xml'])

    // Publish the build-info to Artifactory:
    server.publishBuildInfo buildInfo
  }

  stage('Build Container') {
    docker.withRegistry('https://docker.fs.usda.gov', 'docker_registry') {

      def image = docker.build("docker.fs.usda.gov/ead/region_6_portal_dev:${env.POM_VERSION}", '--no-cache --pull .')

      image.push()
    }
  }

  stage('Push to cluster') {
     //sh "/usr/local/bin/kubectl set image deployment/evalidator evalidator=docker.fs.usda.gov/fia/evalidator:${env.BUILD_NUMBER}"
     kubernetesDeploy configs: 'deploy/dev-portal.yaml', kubeConfig: [path: ''], kubeconfigId: 'kube_config', secretName: '', ssh: [sshCredentialsId: '*', sshServer: ''], textCredentials: [certificateAuthorityData: '', clientCertificateData: '', clientKeyData: '', serverUrl: 'https://']
  }

  stage('Cleanup Docker') {
    sh 'docker system prune -f'
  }
}
