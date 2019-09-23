package io.logbee.gradle.conda.conda.actions;

import io.logbee.gradle.conda.conda.CondaPluginExtension;
import io.logbee.gradle.conda.plugin.PythonPlugin;
import io.logbee.gradle.conda.python.PythonPluginExtension;
import io.logbee.gradle.conda.python.PythonSourceSet;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.*;
import org.gradle.api.file.FileCollection;
import org.gradle.api.model.ObjectFactory;
import org.gradle.process.ExecResult;
import org.gradle.process.internal.ExecAction;
import org.gradle.process.internal.ExecActionFactory;

import javax.inject.Inject;
import java.io.File;

public class InstallDependenciesAction implements Action<ResolvableDependencies> {

    private final Project project;
    private final CondaPluginExtension condaExtension;
    private final ExecActionFactory execActionFactory;
    private final ObjectFactory objectFactory;

    @Inject
    public InstallDependenciesAction(Project project, ExecActionFactory execActionFactory, ObjectFactory objectFactory) {
        this.project = project;
        this.condaExtension = project.getExtensions().getByType(CondaPluginExtension.class);
        this.execActionFactory = execActionFactory;
        this.objectFactory = objectFactory;
    }

    @Override
    public void execute(ResolvableDependencies resolvableDependencies) {

        final DependencySet dependencies = resolvableDependencies.getDependencies();
        for (Dependency dependency : dependencies) {
            if (dependency instanceof ExternalModuleDependency) {
                installExternalDependency((ExternalModuleDependency) dependency);
            }
            else if (dependency instanceof ProjectDependency) {
                installProjectDependency((ProjectDependency) dependency);
            }
            else throw new IllegalArgumentException("Can not install dependency: '" + dependency.getGroup() + ":" + dependency.getName() + ":" + dependency.getVersion() + "'");
        }
    }

    private void installExternalDependency(ExternalModuleDependency dependency) {

        final ExecAction execAction = execActionFactory.newExecAction();
        final ExecResult result;

        execAction.executable(condaExtension.getMiniconda().getCondaExecutable());
        execAction.args("install", "--yes");
        execAction.args("--prefix", condaExtension.getEnvironmentDir());
        execAction.args(toCondaPackageNotation(dependency));

        result = execAction.execute();

        if (result.getExitValue() != 0) {
            throw new InstallDependencyException("Failed to install dependency: " + dependency);
        }
    }

    private void installProjectDependency(ProjectDependency dependency) {

        final Project dependencyProject = dependency.getDependencyProject();

        dependencyProject.afterEvaluate(project -> {

            if (dependencyProject.getPlugins().hasPlugin(PythonPlugin.class)) {

                final PythonPluginExtension pythonPluginExtension = project.getExtensions().getByType(PythonPluginExtension.class);
                final Configuration apiConfiguration = project.getConfigurations().getByName(PythonPlugin.API_CONFIGURATION_NAME);
                final Configuration defaultConfiguration = project.getConfigurations().getByName(PythonPlugin.DEFAULT_CONFIGURATION_NAME);

                execute(apiConfiguration.getIncoming());

                final PythonSourceSet mainSourceSet = pythonPluginExtension.getSourceSets().getByName(PythonPlugin.MAIN_SOURCE_SET_NAME);
                final FileCollection sources = mainSourceSet.getSources();

                sources.getFiles().forEach(file -> {

                    final ExecAction execAction = execActionFactory.newExecAction();
                    final ExecResult result;

                    execAction.executable(condaExtension.getMiniconda().getCondaExecutable());
                    execAction.args("develop");
                    execAction.args("--prefix", condaExtension.getEnvironmentDir());
                    execAction.args(file);

                    result = execAction.execute();

                    if (result.getExitValue() != 0) {
                        throw new InstallDependencyException("Failed to install dependency: " + dependency);
                    }
                });
            }
        });
    }

    private String toCondaPackageNotation(ExternalModuleDependency dependency) {

        final StringBuilder result = new StringBuilder();
        final String group = dependency.getGroup();
        final String name = dependency.getName();
        final String version = dependency.getVersion();

        if (group != null && !group.isEmpty()) {
            result.append(group);
            result.append("::");
        }

        result.append(name);

        if (version != null && !version.isEmpty()) {
            result.append("==");
            result.append(version);
        }

        return result.toString();
    }
}
