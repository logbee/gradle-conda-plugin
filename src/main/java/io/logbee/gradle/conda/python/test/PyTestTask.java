package io.logbee.gradle.conda.python.test;

import io.logbee.gradle.conda.conda.CondaPluginExtension;
import io.logbee.gradle.conda.python.PythonPluginExtension;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.SourceDirectorySet;
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

    public static TaskProvider<PyTestTask> register(ProjectInternal project) {
        final CondaPluginExtension condaExtension = project.getExtensions().getByType(CondaPluginExtension.class);
        final PythonPluginExtension pythonPluginExtension = project.getExtensions().getByType(PythonPluginExtension.class);

        return project.getTasks().register("test", PyTestTask.class, task -> {

            final File pyTestExecutable = new File(condaExtension.getEnvironmentDir(), "bin/pytest");
            final SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
            final SourceSet mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME);
            final SourceSet testSourceSet = sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME);
            final File outputDir = new File(project.getBuildDir(), "test");

            task.setGroup("Verification");
            task.setDescription("Runs the unit tests.");
            task.setPyTestExecutable(pyTestExecutable);
            task.getMainSources().from(mainSourceSet.getExtensions().getByName("python"));
            task.getTestSources().from(testSourceSet.getExtensions().getByName("python"));
            task.setOutputDir(outputDir);
            task.setReportFile(new File(outputDir, "junit-report.xml"));
            task.setIniFile(new File(outputDir, "pytest.ini"));
        });
    }

    private final ExecActionFactory execActionFactory;

    private File pyTestExecutable;

    private final ConfigurableFileCollection mainSources;
    private final ConfigurableFileCollection testSources;
    private File outputDir;
    private File iniFile;
    private File reportFile;

    private boolean ignoreFailures = false;

    @Inject
    public PyTestTask(ExecActionFactory execActionFactory) {
        this.execActionFactory = execActionFactory;
        this.mainSources = getProject().files();
        this.testSources = getProject().files();
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

        if (!getMainSources().isEmpty()) {
            final StringBuilder pythonPath = new StringBuilder();
            for (File file : getMainSources().getFiles()) {
                pythonPath.append(file.getPath()).append(":");
            }
            execAction.environment("PYTHONPATH", pythonPath);
        }

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
    public ConfigurableFileCollection getMainSources() {
        return mainSources;
    }

    @InputFiles
    @SkipWhenEmpty
    public ConfigurableFileCollection getTestSources() {
        return testSources;
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
