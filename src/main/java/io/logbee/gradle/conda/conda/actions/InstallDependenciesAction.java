package io.logbee.gradle.conda.conda.actions;

import io.logbee.gradle.conda.conda.CondaPluginExtension;
import io.logbee.gradle.conda.plugin.PythonPlugin;
import io.logbee.gradle.conda.python.PythonPluginExtension;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.*;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.SourceSet;
import org.gradle.process.ExecResult;
import org.gradle.process.internal.ExecAction;
import org.gradle.process.internal.ExecActionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class InstallDependenciesAction implements Action<ResolvableDependencies> {

    private final Logger log = LoggerFactory.getLogger(InstallDependenciesAction.class);

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
                final ExternalModuleDependency externalModuleDependency = (ExternalModuleDependency) dependency;
                log.info("Installing dependency '{}'.", externalModuleDependency);
                installExternalDependency((ExternalModuleDependency) dependency);
            }
            else if (dependency instanceof ProjectDependency) {
                final Project dependencyProject = ((ProjectDependency) dependency).getDependencyProject();
                log.info("Installing dependency {}.", dependencyProject);
                installProjectDependency((ProjectDependency) dependency);
            }
            else throw new IllegalArgumentException("Can not install dependency: '" + dependency.getGroup() + ":" + dependency.getName() + ":" + dependency.getVersion() + "'");
        }
    }

    private void installExternalDependency(ExternalModuleDependency dependency) {

        final ExecAction execAction;
        final ExecResult result;

        execAction = execActionFactory.newExecAction();
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

            if (project.getPlugins().hasPlugin(PythonPlugin.class)) {

                final PythonPluginExtension pythonPluginExtension = project.getExtensions().getByType(PythonPluginExtension.class);
                final Configuration apiConfiguration = project.getConfigurations().getByName(PythonPlugin.API_CONFIGURATION_NAME);
                final Configuration defaultConfiguration = project.getConfigurations().getByName(PythonPlugin.DEFAULT_CONFIGURATION_NAME);

                execute(apiConfiguration.getIncoming());

                final SourceSet mainSourceSet = pythonPluginExtension.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
                final SourceDirectorySet sources = (SourceDirectorySet) mainSourceSet.getExtensions().findByName("python");

                if (!sources.getFiles().isEmpty()) {

                    sources.getFiles().forEach(file -> {

                        final ExecAction execAction;
                        final ExecResult result;

                        execAction = execActionFactory.newExecAction();
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
                else {
                    log.warn("SourceSet '{}' of {} is empty.", mainSourceSet, dependencyProject);
                }
            }
            else {
                log.warn("Cannot install dependency '{}' because the project does not have the plugin '{}' applied.", dependencyProject, PythonPlugin.class.getSimpleName());
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
