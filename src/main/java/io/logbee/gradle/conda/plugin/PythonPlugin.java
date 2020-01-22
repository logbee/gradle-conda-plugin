package io.logbee.gradle.conda.plugin;

import io.logbee.gradle.conda.conda.actions.InstallDependenciesAction;
import io.logbee.gradle.conda.python.PythonPluginExtension;
import io.logbee.gradle.conda.python.internal.DefaultPythonPluginExtension;
import io.logbee.gradle.conda.python.test.PyTestTask;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.internal.tasks.DefaultSourceSetContainer;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.Delete;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

import javax.inject.Inject;

public class PythonPlugin implements Plugin<ProjectInternal> {

    public static final String DEFAULT_CONFIGURATION_NAME = "default";
    public static final String API_CONFIGURATION_NAME = "api";
    public static final String IMPLEMENTATION_CONFIGURATION_NAME = "implementation";
    public static final String TEST_CONFIGURATION_NAME = "test";

    private final ObjectFactory objectFactory;

    @Inject
    public PythonPlugin(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    @Override
    public void apply(ProjectInternal project) {
        project.getPlugins().apply(CondaPlugin.class);

        addExtensions(project);
        configureSourceSets(project);
        createConfigurations(project);
        registerTasks(project);

        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(Project project) {
                final InstallDependenciesAction installAction = objectFactory.newInstance(InstallDependenciesAction.class, project);
                installAction.execute(project.getConfigurations().getByName(API_CONFIGURATION_NAME).getIncoming());
                installAction.execute(project.getConfigurations().getByName(IMPLEMENTATION_CONFIGURATION_NAME).getIncoming());
                installAction.execute(project.getConfigurations().getByName(TEST_CONFIGURATION_NAME).getIncoming());
            }
        });
    }

    private void addExtensions(final ProjectInternal project) {
        final PythonPluginExtension extension = project.getExtensions().create(PythonPluginExtension.class, "python", DefaultPythonPluginExtension.class, project, objectFactory);
    }

    private void configureSourceSets(Project project) {
        SourceSetContainer sourceSets = project.getExtensions().findByType(SourceSetContainer.class);

        if (sourceSets == null) {
            sourceSets = project.getExtensions().create("sourceSets", DefaultSourceSetContainer.class);
        }

        if (sourceSets.findByName(SourceSet.MAIN_SOURCE_SET_NAME) == null) {
            sourceSets.create(SourceSet.MAIN_SOURCE_SET_NAME);
        }

        if (sourceSets.findByName(SourceSet.TEST_SOURCE_SET_NAME) == null) {
            sourceSets.create(SourceSet.TEST_SOURCE_SET_NAME);
        }

        sourceSets.all(sourceSet -> {
            final SourceDirectorySet sources = objectFactory.sourceDirectorySet(sourceSet.getName(), String.format("%s Python source", sourceSet.getName()));
            sources.srcDir("src/" + sourceSet.getName() + "/python");
            sources.getFilter().include("**/*.py");
            sourceSet.getExtensions().add("python", sources);
            final SourceDirectorySet resources = objectFactory.sourceDirectorySet(sourceSet.getName(), String.format("%s Python resources", sourceSet.getName()));
            resources.srcDir("src/" + sourceSet.getName() + "/resources");
            resources.getFilter().include("**/*.*");
            sourceSet.getExtensions().add("resources", sources);
        });
    }

    private void createConfigurations(Project project) {

        final InstallDependenciesAction installAction = objectFactory.newInstance(InstallDependenciesAction.class, project);
        final ConfigurationContainer configurationContainer = project.getConfigurations();
        final Configuration defaultConfiguration;
        final Configuration apiConfiguration;
        final Configuration implementationConfiguration;
        final Configuration testConfiguration;

        defaultConfiguration = configurationContainer.create(DEFAULT_CONFIGURATION_NAME);

        apiConfiguration = configurationContainer.create(API_CONFIGURATION_NAME);
        apiConfiguration.setCanBeResolved(false);
        apiConfiguration.getIncoming().beforeResolve(installAction);

        implementationConfiguration = configurationContainer.create(IMPLEMENTATION_CONFIGURATION_NAME);
        implementationConfiguration.setCanBeResolved(false);
        implementationConfiguration.getIncoming().beforeResolve(installAction);

        testConfiguration = configurationContainer.create(TEST_CONFIGURATION_NAME);
        testConfiguration.setCanBeResolved(false);
        testConfiguration.extendsFrom(apiConfiguration, implementationConfiguration);
        testConfiguration.getIncoming().beforeResolve(installAction);
    }

    private void registerTasks(ProjectInternal project) {
        PyTestTask.register(project);
        registerCleanTask(project);
    }

    private void registerCleanTask(Project project) {
        if (project.getTasks().findByName("clean") == null) {
            project.getTasks().register("clean", Delete.class, task -> {
                task.setGroup("Build");
                task.setDescription("Deletes the build directory.");
                task.delete(project.getBuildDir());
            });
        }
    }
}
