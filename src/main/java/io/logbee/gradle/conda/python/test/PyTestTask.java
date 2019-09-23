package io.logbee.gradle.conda.python.test;

import io.logbee.gradle.conda.conda.CondaPluginExtension;
import io.logbee.gradle.conda.conda.actions.InstallDependencyException;
import io.logbee.gradle.conda.plugin.PythonPlugin;
import io.logbee.gradle.conda.python.PythonPluginExtension;
import io.logbee.gradle.conda.python.PythonSourceSet;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.VerificationTask;
import org.gradle.process.ExecResult;
import org.gradle.process.ProcessForkOptions;
import org.gradle.process.internal.ExecAction;
import org.gradle.process.internal.ExecActionFactory;

import javax.inject.Inject;
import java.io.File;

public class PyTestTask extends /*AbstractExecTask<TestPythonTask>*/ DefaultTask implements VerificationTask {

    public static PyTestTask create(ProjectInternal project) {
        PyTestTask task = project.getTasks().create("test", PyTestTask.class, project);
        task.setGroup("verification");
        task.setDescription("Run tests.");
        return task;
    }

    private final Project project;
    private final ExecActionFactory execActionFactory;

    private boolean ignoreFailures = false;

    @Inject
    public PyTestTask(Project project, ExecActionFactory execActionFactory) {

        this.project = project;
        this.execActionFactory = execActionFactory;

        final CondaPluginExtension condaExtension = project.getExtensions().getByType(CondaPluginExtension.class);
        final PythonPluginExtension pythonPluginExtension = project.getExtensions().getByType(PythonPluginExtension.class);
        final PythonSourceSet testSourceSet = pythonPluginExtension.getSourceSets().getByName(PythonPlugin.TEST_SOURCE_SET_NAME);
        final File pytestExecutable = new File(condaExtension.getEnvironmentDir(), "bin/pytest");

        setOnlyIf(new Spec<Task>() {
            @Override
            public boolean isSatisfiedBy(Task element) {
                return pytestExecutable.exists() && !testSourceSet.getSources().getAsFileTree().getFiles().isEmpty();
            }
        });

        doLast(new Action<Task>() {

            @Override
            public void execute(Task task) {

                final ExecAction execAction = execActionFactory.newExecAction();
                final ExecResult result;

                execAction.executable(pytestExecutable);
                execAction.args(testSourceSet.getSources().getFiles());
                result = execAction.execute();
            }
        });
    }

    @Override
    public void setIgnoreFailures(boolean ignoreFailures) {
        this.ignoreFailures = ignoreFailures;
    }

    @Override
    public boolean getIgnoreFailures() {
        return ignoreFailures;
    }
}
