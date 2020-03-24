import jetbrains.buildServer.configs.kotlin.v2019_2.*

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2019.2"

project {

    description = "case 2545335"
	
	buildType(DevBranchRoma980)
    buildType(DevBranchCurtBuildAll)
    buildType(DevBranchCurtSnapshot)
    buildType(DevBranchCurtAustin980)
    buildType(DevBranchCurtBryan980)
    buildType(DevBranchCurtMidland980)
    buildType(DevBranchBandera980)

    template(DockerMainParameters)
    template(FwBvatWithoutPluginList)
}

object DevBranchBandera980 : BuildType({
    templates(DockerMainParameters, FwBvatWithoutPluginList)
    name = "Dev Branch - Curt Bandera 9.8.0"

    params {
        param("env.BUILD_PLID", "AH")
        param("env.MAKE_EXTRA_VARIABLES", "BUILD_PLID=%env.BUILD_PLID% CHANGELIST_STAMP=%build.vcs.number% STRIP_DEBUG=false")
        param("env.MYTESTVAL", "something else more")
        param("env.platform", "bandera")
        param("env.PATH", "/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin:/root/bin")
        param("env.MAKE_ENV_PARAMS", "ENABLE_GCC8=y PN=app BUILD_VERSION=%BuildCounter%")
        param("env.PRODUCT_PLATFORM", "Bandera_TCL_TCL")
        param("env.MAKE_EXTRA_TGTS", "dist-package")
        param("platform", "bandera")
    }
    
    disableSettings("RQ_584")
})

object DevBranchCurtAustin980 : BuildType({
    templates(DockerMainParameters, FwBvatWithoutPluginList)
    name = "Dev Branch - Curt Austin 9.8.0"

    params {
        param("env.PRODUCT_PLATFORM", "Austin")
        param("env.platform", "austin")
        param("platform", "austin")
    }
    
    disableSettings("RQ_584")
})

object DevBranchCurtBryan980 : BuildType({
    templates(DockerMainParameters, FwBvatWithoutPluginList)
    name = "Dev Branch - Curt Bryan 9.8.0"

    params {
        param("env.PRODUCT_PLATFORM", "Bryan")
        param("platform", "bryan")
    }
    
    disableSettings("RQ_584")
})

object DevBranchCurtBuildAll : BuildType({
    name = "Dev Branch Curt Build All"

    buildNumberPattern = "${DevBranchCurtSnapshot.depParamRefs.buildNumber}"

    vcs {
        root(DslContext.settingsRoot)

        checkoutMode = CheckoutMode.MANUAL
    }

    triggers {
        schedule {
            enabled = false
            schedulingPolicy = daily {
                hour = 18
                timezone = "America/Los_Angeles"
            }
            branchFilter = ""
            triggerBuild = always()
        }
    }

    dependencies {
        snapshot(DevBranchBandera980) {
        }
        snapshot(DevBranchCurtAustin980) {
        }
        snapshot(DevBranchCurtBryan980) {
        }
        snapshot(DevBranchCurtMidland980) {
        }
        snapshot(DevBranchCurtSnapshot) {
        }
        snapshot(DevBranchRoma980) {
        }
    }

    requirements {
        exists("AWS_AGENT")
    }
})

object DevBranchCurtMidland980 : BuildType({
    templates(DockerMainParameters, FwBvatWithoutPluginList)
    name = "Dev Branch - Curt Midland 9.8.0"

    params {
        param("env.PRODUCT_PLATFORM", "Mid_TCL_TCL,Mid_TCL_Ins,Mid_TCL_Phi,Mid_Fun_Mag,Mid_Fun_Phi,Mid_Fun_San,Mid_His_Shp_Bby,Mid_TPV_Shp,Mid_TPV_Ins,Mid_TPV_Ele,Mid_TCL_Hit,Mid_TCL_Onn,Mid_CVTE_RCA,Mid_His_His,Mid_MTC_JVC,Mid_CVTE-Fun_San,Mid_CVTE-Fun_Phi,Mid_CVTE-Fun_Mag,Mid_CNC_Onn,Mid_MTC_WH,Mid_MTC_Onn,Mid_CH_Onn,Mid_MTC_Turnkey,Mid_TCL_Multiple")
        param("platform", "midland")
        param("env.PATH", "/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin:/root/bin")
    }
    
    disableSettings("RQ_584")
})

object DevBranchCurtSnapshot : BuildType({
    name = "Dev Branch Curt Snapshot"

    maxRunningBuilds = 1

    vcs {
        root(DslContext.settingsRoot)

        checkoutMode = CheckoutMode.MANUAL
    }

    steps {
        script {
            name = "10 sec delay"
            scriptContent = "sleep 10"
        }
    }

    requirements {
        exists("AWS_AGENT")
    }
})

object DevBranchRoma980 : BuildType({
    templates(DockerMainParameters, FwBvatWithoutPluginList)
    name = "Dev Branch - Curt Roma 9.8.0"

    params {
        param("BUILD_PLID", "AV")
        param("env.platform", "roma")
        param("env.PATH", "/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin:/root/bin")
        param("env.PRODUCT_PLATFORM", "Roma_MTC_Onn,Roma_CH_Philco")
        param("platform", "roma")
    }
    
    disableSettings("RQ_584")
})

object DockerMainParameters : Template({
    name = "Docker Main Parameters"

    allowExternalStatus = true
    maxRunningBuilds = 4

    params {
        param("DOCKER_IMAGE", "bar.artifactory.tools.roku.com/buildtools/firmware/main/buildtoolsciamazonlinux:2687406-2689351")
        param("env.MAJOR", "9")
        param("env.MAKE_EXTRA_VARIABLES", """""""")
        param("DOCKER_RUN_ARGS", """
            -u ${'$'}USER
            -e USER=${'$'}USER
            -e LANG=${'$'}LANG
            -e MINOR=${'$'}MINOR
            -e MAJOR=${'$'}MAJOR
            -e BVAT_URL=${'$'}BVAT_URL
            -e ADD_TARGET=${'$'}ADD_TARGET
            -e PRODUCT_PLATFORM=${'$'}PRODUCT_PLATFORM
            -e ICECC_DISABLED=1
            -v /keys:/keys
            -v /certs:/certs
            -v /tmp:/tmp
            --network host
        """.trimIndent())
        param("saved_cmd", "%env.MAKE_ENV_PARAMS% make -j`nproc` BUILD_PLATFORM=%platform% %env.MAKE_EXTRA_VARIABLES% rootfs port-image %env.MAKE_EXTRA_TGTS% publish")
        param("BuildCounter", "${DevBranchCurtSnapshot.depParamRefs.buildNumber}")
        param("env.MINOR", "80")
        param("env.PATH", "/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin:/root/bin")
        param("env.MAKE_ENV_PARAMS", """""""")
        param("PluginList", """""""")
        param("env.MAKE_EXTRA_TGTS", """""""")
        param("bld_cmd", "%env.MAKE_ENV_PARAMS% make -j`nproc` BUILD_PLATFORM=%platform% %env.MAKE_EXTRA_VARIABLES% rootfs port-image %env.MAKE_EXTRA_TGTS% publish")
    }

    vcs {
        root(DslContext.settingsRoot)

        checkoutMode = CheckoutMode.ON_AGENT
        cleanCheckout = true
    }

    steps {
        script {
            name = "Build w/ Docker"
            id = "RUNNER_492"
            scriptContent = """
                echo Build Command is:
                echo %bld_cmd%
                cd os
                %bld_cmd%
                #%env.MAKE_ENV_PARAMS% make -j`nproc` BUILD_PLATFORM=%platform% %env.MAKE_EXTRA_VARIABLES% rootfs port-image %env.MAKE_EXTRA_TGTS% publish
            """.trimIndent()
            dockerImage = "%DOCKER_IMAGE%"
            dockerPull = true
            dockerRunParameters = "%DOCKER_RUN_ARGS%"
        }
        script {
            name = "Publish w/ Docker"
            id = "RUNNER_493"
            enabled = false
            scriptContent = """
                #Don't publish anything for personal builds
                # [ "${'$'}{BUILD_IS_PERSONAL}" = "true" ] && exit 0
                
                #PluginList should always be set to "" in TeamCity configurations.
                #If PluginList is injected by the build then publish plugins listed in the file
                [ -z %PluginList% ] || plgn_publish.sh -f %PluginList% || exit 1
                
                if bvat_publish.sh %build.number% "%system.teamcity.buildConfName%" %build.vcs.number% %env.PRODUCT_PLATFORM% BVAT %BVAT_URL% ${'$'}{PPV}; then
                     echo "triggered BVAT"
                else
                     echo "##teamcity[buildStatus status='SUCCESS' text='BVAT failed']"
                fi
            """.trimIndent()
            dockerImage = "%DOCKER_IMAGE%"
            dockerPull = true
            dockerRunParameters = "%DOCKER_RUN_ARGS%"
        }
    }

    failureConditions {
        executionTimeoutMin = 180
    }

    dependencies {
        snapshot(DevBranchCurtSnapshot) {
        }
    }

    requirements {
        equals("BUILDTYPE", "official", "RQ_1373")
        equals("DOCKER_AGENT", "true", "RQ_1374")
        equals("AMAZONLINUX_AGENT", "true", "RQ_1375")
    }
})

object FwBvatWithoutPluginList : Template({
    name = "FW BVAT without PluginList"

    allowExternalStatus = true
    buildNumberPattern = "%BuildCounter%"
    maxRunningBuilds = 4

    params {
        param("BuildCounter", "{0}")
        param("BVAT_URL", "http://ra-jenkins.corp.roku:8080/job/bvat_master_publisher/buildWithParameters")
        param("env.BVAT_URL", "true")
        param("env.IS_TEAMCITY_BUILD", "true")
    }

    vcs {
        checkoutMode = CheckoutMode.ON_AGENT
        cleanCheckout = true
    }

    steps {
        script {
            name = "Cleanup"
            id = "RUNNER_21"
            executionMode = BuildStep.ExecutionMode.ALWAYS
            scriptContent = "/bin/rm -fr %teamcity.agent.work.dir%/%teamcity.build.default.checkoutDir%/*"
        }
    }

    failureConditions {
        executionTimeoutMin = 180
    }

    features {
        freeDiskSpace {
            id = "jetbrains.agent.free.space"
            requiredSpace = "100gb"
            failBuild = false
        }
    }
})


