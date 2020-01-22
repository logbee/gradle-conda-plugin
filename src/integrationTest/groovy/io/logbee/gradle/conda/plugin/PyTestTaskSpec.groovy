package io.logbee.gradle.conda.plugin


import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class PyTestTaskSpec extends ExampleProjectFixture {

    @Rule final TemporaryFolder testProjectDir = new TemporaryFolder(File.createTempDir("gradle-conda-plugin-PyTestTaskSpec-", "")) {
        @Override
        protected void after() {
//            super.after() // Comment this line to keep the tmp folder.
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
            |      srcDir 'src'
            |    }
            |  }
            |  test {
            |    python {
            |      srcDir 'test'
            |    }
            |  }
            |}
            | 
            |dependencies {
            |  api project(':example-lib')
            |  test 'conda-forge:pytest:5.1.2'
            |}
            |""".stripMargin()

        when:
        def pytest = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withDebug(true)
                .withArguments('example-app:test', '--info', '--stacktrace')
                .build()

        then:
        println(pytest.output)
        pytest.output.contains("test session starts")
        pytest.output.contains("1 passed")
        pytest.output.contains('BUILD SUCCESSFUL')
    }
}
