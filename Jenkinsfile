
pipeline {
  agent {
      label 'general'
  }

  environment {
    PROJECT_DIR = "."
    //setup environment for node install
    NODEJS_ORG_MIRROR = 'https://centralrepo.brz.gv.at/artifactory/nodejs-dist-mirror'
    SASS_BINARY_SITE = 'https://centralrepo.brz.gv.at/artifactory/node-sass-mirror/sass/node-sass/releases/download'
    npm_registry = 'https://centralrepo.brz.gv.at/artifactory/api/npm/npm-npmjs/'
    NO_PROXY = 'centralrepo.brz.gv.at'
    nodeVersion = 'v16.16.0'
    npmVersion = '7.0.0'
    nodeDistro = 'linux-x64'
    PATH = "${WORKSPACE}/node/bin:${WORKSPACE}/node_modules/.bin/:$PATH"

  //depndency checks:
    NVD_CVE_LOCAL_DB_URL = "https://centralrepo.brz.gv.at/artifactory/brz-nvd-cve-local/"
    NVD_CVE_JSON_MIRROR_URL = "https://centralrepo.brz.gv.at/artifactory/nvd-cve-json-mirror/"

    AUTHOR = "ju-eu-jenkins <jueu-jenkins@brz.gv.at>"
      // Lokales Maven Repo. Hier werden die Artefakte für den Deploy am Artifactory geholt

  }

  tools {
      jdk 'OpenJDK8'
      maven 'Maven 3.8'
  }

  stages {
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

    stage ('prepare release') {
      when {
        expression {
          return env.BRANCH_NAME.startsWith('dofixrelease') || env.BRANCH_NAME.startsWith('dorelease')
        }
      }
      steps {
        withMaven {
          script {
              def releaseVersion = at.gv.brz.jueu.tools.VersionFromBranchHelper.getVersionFromBranch(env.BRANCH_NAME)
            //TODO: set releaseVersion!!!
              // sh('mvn failsafe:integration-test')
              sh("mvn -U -e -V build-helper:parse-version versions:set -DnewVersion=${releaseVersion}")
              sh("mvn -U -e -V com.github.ekryd.sortpom:sortpom-maven-plugin:2.15.0:sort")
              def git = new at.gv.brz.jueu.tools.Git(this)
              git("add -u")
              git("commit --allow-empty -m 'commit version updated ${releaseVersion}' --author '${AUTHOR}'")
              sh("git tag -a v${releaseVersion} -m 'creating release tag'")
            }
          }
      }
    }


    stage('mvn install') {
      steps {
        dir("${PROJECT_DIR}") {
          withMaven {
            sh('mvn -e -U -V -T2C -Dmaven.test.failure.ignore=true install')
          }
        }
      }
    }


    stage ('deploy release via [mvn deploy]') {
      when {
        expression {
          return env.BRANCH_NAME.startsWith('dofixrelease') || env.BRANCH_NAME.startsWith('dorelease')
        }
      }
      steps {
        withMaven {
          script {
              // def releaseVersion = at.gv.brz.jueu.tools.VersionFromBranchHelper.getVersionFromBranch(env.BRANCH_NAME)

              sh("mvn -e -U -V -Pjavadoc deploy -X")
              def git = new at.gv.brz.jueu.tools.Git(this)
              git("push --tags origin HEAD:${env.BRANCH_NAME}" )

              //TODO: merge back delete branch....
            }
          }
      }
    }
    stage ('merge release branch back') {
      when {
        expression {
          return env.BRANCH_NAME.startsWith('dofixrelease') || env.BRANCH_NAME.startsWith('dorelease')
        }
      }
      steps {
        dir("${PROJECT_DIR}") {
          withMaven {
            script {
              def git = new at.gv.brz.jueu.tools.Git(this)
              def utils = new at.gv.brz.jueu.tools.Utils(this)
              def branchName = env.BRANCH_NAME
              def releaseVersion = utils.getVersionFromBranch(branchName)
              def devBranchName = utils.getDevBranchName(releaseVersion)

              git("reset --hard")
              git("checkout -- .")
              git("checkout ${devBranchName}")
              git("merge --ff-only ${branchName}")
              def incrementedVersion = utils.incrementPatchVersionNumber(releaseVersion)
              sh("build-helper:parse-version versions:set -DnewVersion=${incrementedVersion}-SNAPSHOT")
              git("add -u")
              git("commit -m 'commit snapshot version ${incrementedVersion} in pom.xml' --author '${AUTHOR}'")
              git("push")

              git("push origin :${branchName}")

              //TODO: merge back delete branch....
            }
          }
        }
      }
    }

    stage ('delete release branch') {
      when {
        expression {
          return env.BRANCH_NAME.startsWith('dofixrelease') || env.BRANCH_NAME.startsWith('dorelease')
        }
      }
      steps {
        withMaven {
          script {
            def git = new at.gv.brz.jueu.tools.Git(this)
            git("push origin :${branchName}")
          }
        }
      }
    }
  }
  post {
    always {
      junit '**/test-reports/*.xml,**/surefire-reports/*.xml,**/failsafe-reports/*.xml,**/dbunit-reports/*.xml,**/unittest-reports/*.xml,**/ittest-reports/*.xml'  //publish test reports
    }
  }
}

