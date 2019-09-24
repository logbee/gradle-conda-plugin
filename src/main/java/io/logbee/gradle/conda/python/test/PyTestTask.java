package io.logbee.gradle.conda.python.test;

import io.logbee.gradle.conda.conda.CondaPluginExtension;
import io.logbee.gradle.conda.plugin.PythonPlugin;
import io.logbee.gradle.conda.python.PythonPluginExtension;
import io.logbee.gradle.conda.python.PythonSourceSet;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.tasks.*;
import org.gradle.process.ExecResult;
import org.gradle.process.internal.ExecAction;
import org.gradle.process.internal.ExecActionFactory;

import javax.inject.Inject;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class PyTestTask extends DefaultTask {

    public static PyTestTask create(ProjectInternal project) {
        final CondaPluginExtension condaExtension = project.getExtensions().getByType(CondaPluginExtension.class);
        final PythonPluginExtension pythonPluginExtension = project.getExtensions().getByType(PythonPluginExtension.class);
        final PythonSourceSet mainSourceSet = pythonPluginExtension.getSourceSets().getByName(PythonPlugin.MAIN_SOURCE_SET_NAME);
        final PythonSourceSet testSourceSet = pythonPluginExtension.getSourceSets().getByName(PythonPlugin.TEST_SOURCE_SET_NAME);
        final File pyTestExecutable = new File(condaExtension.getEnvironmentDir(), "bin/pytest");
        final File outputDir = new File(project.getBuildDir(), "test");

        return project.getTasks().create("test", PyTestTask.class, task -> {
            task.setGroup("verification");
            task.setDescription("Run tests.");
            task.setPyTestExecutable(pyTestExecutable);
            task.setMainSources(mainSourceSet.getSources());
            task.setTestSources(testSourceSet.getSources());
            task.setOutputDir(outputDir);
            task.setReportFile(new File(outputDir, "junit-report.xml"));
            task.setIniFile(new File(outputDir, "pytest.ini"));
        });
    }

    private final ExecActionFactory execActionFactory;

    private File pyTestExecutable;

    private FileCollection mainSources;
    private FileCollection testSources;
    private File outputDir;
    private File iniFile;
    private File reportFile;

    private boolean ignoreFailures = false;

    @Inject
    public PyTestTask(ExecActionFactory execActionFactory) {
        this.execActionFactory = execActionFactory;
    }

    @TaskAction
    public void execute() {
        writeIniFile();
        executePyTest();
    }

    private void writeIniFile() {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getIniFile(), false))) {

            writer.append("[pytest]").append('\n');
            writer.append('\n');
            writer.flush();
        }
        catch (Exception ignored) {

        }
    }

    private void executePyTest() {
        final ExecAction execAction = execActionFactory.newExecAction();
        final ExecResult result;

        execAction.executable(getPyTestExecutable());

        final StringBuilder pythonPath = new StringBuilder();
        for (File file : getMainSources().getFiles()) {
            pythonPath.append(file.getPath()).append(":");
        }
        execAction.environment("PYTHONPATH", pythonPath);

        execAction.args("--rootdir", getOutputDir());
        execAction.args("-c", getIniFile());
        execAction.args("--junitxml=" + getReportFile());

        if (!getIgnoreFailures()) {
            execAction.args("--exitfirst");
        }

        execAction.args(getTestSources());

//        Exit code 0:	All tests were collected and passed successfully
//        Exit code 1:	Tests were collected and run but some of the tests failed
//        Exit code 2:	Test execution was interrupted by the user
//        Exit code 3:	Internal error happened while executing tests
//        Exit code 4:	pytest command line usage error
//        Exit code 5:	No tests were collected

        result = execAction.execute();
    }

    @InputFile
    public File getPyTestExecutable() {
        return pyTestExecutable;
    }

    public void setPyTestExecutable(File pyTestExecutable) {
        this.pyTestExecutable = pyTestExecutable;
    }

    @InputFiles
    public FileCollection getMainSources() {
        return mainSources;
    }

    public void setMainSources(FileCollection mainSources) {
        this.mainSources = mainSources;
    }

    @InputFiles
    public FileCollection getTestSources() {
        return testSources;
    }

    public void setTestSources(FileCollection testSources) {
        this.testSources = testSources;
    }

    @OutputDirectory
    public File getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir;
    }

    @OutputFile
    public File getIniFile() {
        return iniFile;
    }

    public void setIniFile(File iniFile) {
        this.iniFile = iniFile;
    }

    @OutputFile
    public File getReportFile() {
        return reportFile;
    }

    public void setReportFile(File file) {
        this.reportFile = file;
    }

    @Input
    public boolean getIgnoreFailures() {
        return ignoreFailures;
    }

    public void setIgnoreFailures(boolean ignoreFailures) {
        this.ignoreFailures = ignoreFailures;
    }
}
