package io.logbee.gradle.conda.plugin


import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class PythonPluginPyTestSpec extends ExampleProjectFixture {

    @Rule final TemporaryFolder testProjectDir = new TemporaryFolder(new File("/home/elschug/Projects/gradle-conda-plugin/tmp")) {
        @Override
        protected void after() {
            super.after() // Comment this line to keep the tmp folder.
        }
    }

    def "build a python project"() {

        given:
        settingsFile << """
            |rootProject.name = 'gradle-conda-example'
            |include ':example-lib'
            |include ':example-app'
            |""".stripMargin()

        buildFile << """
        """.stripMargin()

        libProjectBuildFile << """
            |plugins {
            |  id 'io.logbee.gradle.python'
            |}
            |
            |version = "0.1.0"
            |
            |dependencies {
            |  api 'conda-forge:protobuf:3.8.0'
            |}
            |""".stripMargin()

        appProjectBuildFile << """
            |plugins {
            |  id 'io.logbee.gradle.python'
            |}
            |
            |version = "0.1.0"
            |
            |sourceSets {
            |  main {
            |    python {
            |      include 'src'
            |    }
            |  }
            |  test {
            |    python {
            |      include 'test'
            |    }
            |  }
            |}
            | 
            |dependencies {
            |  api project(':example-lib')
            |  //implementation 'conda-forge:scipy:1.3.1'
            |  test 'conda-forge:pytest:5.1.2'
            |}
            |""".stripMargin()

        when:
        def bootstrap = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withDebug(true)
                .withArguments('test', '--info', '--stacktrace')
                .build()

        then:
        println(bootstrap.output)
        bootstrap.output.contains('BUILD SUCCESSFUL')

//        bootstrap.task(":bootstrapMiniconda").outcome == SUCCESS

//        when:
//        def createCondaEnvironment = GradleRunner.create()
//                .withProjectDir(testProjectDir.root)
//                .withPluginClasspath()
//                .withDebug(true)
//                .withArguments('createCondaEnvironment', '--info', '--stacktrace')
//                .build()

//        then:
//        println(createCondaEnvironment.output)
//        createCondaEnvironment.output.contains('BUILD SUCCESSFUL')
//        createCondaEnvironment.task(":createCondaEnvironment").outcome == SUCCESS
    }
}
