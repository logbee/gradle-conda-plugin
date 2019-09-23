package io.logbee.gradle.conda.plugin

import spock.lang.Specification

class ExampleProjectFixture extends Specification {

    File settingsFile
    File buildFile
    File libProjectDir
    File libProjectBuildFile
    File libSrcDir
    File libSrcFile
    File libResFile
    File appProjectDir
    File appProjectBuildFile
    File appSrcDir
    File appTestDir
    File appSrcFile
    File appTestFile

    def setup() {
        settingsFile = testProjectDir.newFile('settings.gradle')
        buildFile = testProjectDir.newFile('build.gradle')
        libProjectDir = testProjectDir.newFolder('example-lib')
        libProjectBuildFile = new File(libProjectDir,'build.gradle')
        libProjectBuildFile.createNewFile()
        libSrcDir = testProjectDir.newFolder('example-lib', 'src')
        libSrcFile = new File(libSrcDir, 'lib.py')
        libSrcFile.createNewFile()
        libSrcFile << getClass().getResource( '/example-lib/lib.py' ).text
        libResFile = new File(libSrcDir, 'readme.adoc')
        libResFile.createNewFile()
        appProjectDir = testProjectDir.newFolder('example-app')
        appProjectBuildFile = new File(appProjectDir,'build.gradle')
        appProjectBuildFile.createNewFile()
        appSrcDir = testProjectDir.newFolder('example-app', 'src')
        appTestDir = testProjectDir.newFolder('example-app', 'test')
        appSrcFile = new File(appSrcDir, 'example.py')
        appSrcFile.createNewFile()
        appSrcFile << getClass().getResource( '/example-app/example.py' ).text
        appTestFile = new File(appTestDir, 'test_example.py')
        appTestFile.createNewFile()
        appTestFile << getClass().getResource( '/example-app/test_example.py' ).text
    }
}
