package io.logbee.gradle.conda.plugin;

import io.logbee.gradle.conda.conda.actions.InstallDependenciesAction;
import io.logbee.gradle.conda.python.PythonPluginExtension;
import io.logbee.gradle.conda.python.PythonSourceSet;
import io.logbee.gradle.conda.python.PythonSourceSetContainer;
import io.logbee.gradle.conda.python.internal.DefaultPythonPluginExtension;
import io.logbee.gradle.conda.python.test.PyTestTask;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.model.ObjectFactory;

import javax.inject.Inject;

import static java.util.Collections.singleton;

public class PythonPlugin implements Plugin<ProjectInternal> {

    public static final String DEFAULT_CONFIGURATION_NAME = "default";
    public static final String API_CONFIGURATION_NAME = "api";
    public static final String IMPLEMENTATION_CONFIGURATION_NAME = "implementation";
    public static final String TEST_CONFIGURATION_NAME = "test";

    public static String MAIN_SOURCE_SET_NAME = "main";
    public static String TEST_SOURCE_SET_NAME = "test";

    private final ObjectFactory objectFactory;

    @Inject
    public PythonPlugin(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    @Override
    public void apply(ProjectInternal project) {
        project.getPlugins().apply(CondaPlugin.class);

        addExtensions(project);
        createConfigurations(project);
        configureSourceSets(project);
        createTasks(project);

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

    private void createTasks(ProjectInternal project) {
        PyTestTask.create(project);
    }

    private void addExtensions(final ProjectInternal project) {
        final PythonPluginExtension extension = project.getExtensions().create(PythonPluginExtension.class, "python", DefaultPythonPluginExtension.class, project, objectFactory);
        project.getExtensions().add(PythonSourceSetContainer.class, "sourceSets", extension.getSourceSets());
    }

    private void configureSourceSets(Project project) {
        final PythonPluginExtension extension = project.getExtensions().getByType(PythonPluginExtension.class);
        final PythonSourceSet main = extension.getSourceSets().create(MAIN_SOURCE_SET_NAME);
        final PythonSourceSet test = extension.getSourceSets().create(TEST_SOURCE_SET_NAME);

        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(Project project) {
                addDefaultIfEmpty(main.getPython(), "src");
                main.getResources().setSrcDirs(main.getPython().getSrcDirs());
                addDefaultIfEmpty(test.getPython(), "test");
                test.getResources().setSrcDirs(test.getPython().getSrcDirs());
            }

            private void addDefaultIfEmpty(SourceDirectorySet sourceDirectorySet, String path) {
                if (sourceDirectorySet.isEmpty()) sourceDirectorySet.setSrcDirs(singleton(path));
            }
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
}
