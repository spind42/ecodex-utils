pipeline {
  options {
    timeout(time: 1, unit: 'HOURS')
  }
  agent {
      label 'general'
  }

  environment {
    PROJECT_DIR = "./"
    NODE_OPTIONS='--openssl-legacy-provider'
    //setup environment for node install
    NODEJS_ORG_MIRROR = 'https://centralrepo.brz.gv.at/artifactory/nodejs-dist-mirror'
    SASS_BINARY_SITE = 'https://centralrepo.brz.gv.at/artifactory/node-sass-mirror/sass/node-sass/releases/download'
    npm_registry = 'https://centralrepo.brz.gv.at/artifactory/api/npm/npm-npmjs/'
    NO_PROXY = 'centralrepo.brz.gv.at'
    nodeVersion = 'v18.12.1'
    npmVersion = '9.2.0'
    nodeDistro = 'linux-x64'
    PATH = "${WORKSPACE}/node/bin:${WORKSPACE}/node_modules/.bin/:$PATH"

  //depndency checks:
    NVD_CVE_LOCAL_DB_URL = "https://centralrepo.brz.gv.at/artifactory/brz-nvd-cve-local/"
    NVD_CVE_JSON_MIRROR_URL = "https://centralrepo.brz.gv.at/artifactory/nvd-cve-json-mirror/"
  }

  tools {
      jdk 'OpenJDK11'
      maven 'Maven 3.8'
  }

  stages {

// some block


      stage('checkout') {
        steps {
          checkout scm
        }
      }

      stage('Install Node & NPM') {
        steps {
          withEnv([]) {
            sh('ls -la /maven-cache')
            sh("curl -Ok ${NODEJS_ORG_MIRROR}/${nodeVersion}/node-${nodeVersion}-${nodeDistro}.tar.gz")
            sh("tar -xzf node-${nodeVersion}-${nodeDistro}.tar.gz -C ${WORKSPACE}")
            sh("rm node-${nodeVersion}-${nodeDistro}.tar.gz")
            sh("mv ${WORKSPACE}/node-${nodeVersion}-${nodeDistro} ${WORKSPACE}/node")

            sh '''echo "Using node & npm version: "
                  npm config set registry $npm_registry
                  npm install -g npm@${npmVersion}
                  npm install -g pnpm
                  node --version && npm --version && which node && which npm
              '''
          }
        }
      }

      //TODO: if release build make release preparations...
      //RELEASE_BUILD =


      stage('mvn setup project') {
        steps {
          dir("${PROJECT_DIR}") {
            withMaven {
              sh("mvn versions:update-property -Dproperty=project.build.outputTimestamp -DnewVersion=${env.GIT_COMMIT}${env.BUILD_NUMBER}")
            }
          }
        }
      }

      stage('mvn install') {
        steps {
          dir("${PROJECT_DIR}") {
            withMaven {
              sh('mvn -T2C -Pproduction -Pdocumentation -Dmaven.test.failure.ignore=true install')
            }
          }
        }
      }

      stage('mvn integration tests') {
        steps {
          dir("${PROJECT_DIR}") {
            withMaven {
              sh('mvn -Dmaven.test.failure.ignore=true failsafe:integration-test')
            }
          }
        }
      }

      //TODO: create container and deploy into labenv02 ?
      stage('build image') {
        when {
          expression {
            return DPL_BRANCH.containsKey(BRANCH_NAME)
          }
        }
        steps {
          dir("${PROJECT_DIR}") {
            withCredentials([usernamePassword(credentialsId: 	"ju-eu-jenkins-a3-artifactory", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
              withMaven {
                script  {
                  try {
                    sh('mvn install -pl :domibusConnectorBuilderStandalone -Pjib -Pproduction -DskipTests=true '
                        + ' -Djib.serialize=true '
                        + ' -Djib.from.auth.username=$USERNAME '
                        + ' -Djib.from.auth.password=$PASSWORD '
                        + ' -Djib.to.auth.username=$USERNAME '
                        + ' -Djib.to.auth.password=$PASSWORD -e')
                  } catch (all) {
                    //do nothing if paas deployment failed
                    unstable(message: "${STAGE_NAME} is unstable, uploading image failed")
                  }
                }
              }
            }
          }
        }
      }

      //TODO: create container and deploy into labenv02 ?
      stage('deploy image in paas') {
        when {
          expression {
            return DPL_BRANCH.containsKey(BRANCH_NAME)
          }
        }
        steps {
          dir("${PROJECT_DIR}") {
            withCredentials([string(credentialsId: 	"ju-eu-jenkins-integrator-token", variable: 'OC_TOKEN')]) {
              withMaven {
                script  {
                  try {
                    def labid = DPL_BRANCH[BRANCH_NAME]
                    sh("oc login https://api.a2.cp.cna.at:6443 --token ${OC_TOKEN}; oc project ju-eu-ejustice-eqs")
                    sh("mvn clean initialize validate -pl :domibusConnectorBuilderStandalone -Dlab.id=${labid}")
                    sh("oc apply -f domibusConnectorBuilder/domibusConnectorBuilderStandalone/target/k8s/dpl-template-connector.yaml")
                  } catch (all) {
                    //do nothing if paas deployment failed
                    println all
                    unstable(message: "${STAGE_NAME} is unstable, paas deployment failed")
                  }
                }
              }
            }
          }
        }
      }


  }
  post {
    always {
      junit '**/target/surefire-reports/*.xml'
    }
  }
}
