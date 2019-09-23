package io.logbee.gradle.conda.plugin;

import io.logbee.gradle.conda.conda.CondaPluginExtension;
import io.logbee.gradle.conda.conda.Miniconda;
import io.logbee.gradle.conda.conda.MinicondaExtension;
import io.logbee.gradle.conda.conda.actions.BootstrapMinicondaAction;
import io.logbee.gradle.conda.conda.actions.CreateCondaEnvironmentAction;
import io.logbee.gradle.conda.conda.actions.InstallDependenciesAction;
import io.logbee.gradle.conda.conda.internal.DefaultCondaPluginExtension;
import io.logbee.gradle.conda.conda.internal.DefaultMinicondaExtension;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.model.ObjectFactory;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class CondaPlugin implements Plugin<ProjectInternal> {

    public static final String IVY_REPO_URL = "https://repo.continuum.io";
    public static final String MINICONDA_INSTALLER_CONFIGURATION_NAME = "minicondaInstaller";
    public static final String CONDA_CONFIGURATION_NAME = "conda";

    private final ObjectFactory objectFactory;

    @Inject
    public CondaPlugin(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    @Override
    public void apply(ProjectInternal project) {

        createExtensions(project);
        createConfigurations(project);
        createRepository(project);
        addDependencies(project);

        project.afterEvaluate(objectFactory.newInstance(BootstrapMinicondaAction.class));
        project.afterEvaluate(objectFactory.newInstance(CreateCondaEnvironmentAction.class));
        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(Project project) {
                final InstallDependenciesAction action = objectFactory.newInstance(InstallDependenciesAction.class, project);
                action.execute(project.getConfigurations().getByName(CONDA_CONFIGURATION_NAME).getIncoming());
            }
        });
    }

    private void createExtensions(ProjectInternal project) {
        project.getExtensions().create(CondaPluginExtension.class, "conda", DefaultCondaPluginExtension.class, project);
        project.getExtensions().create(MinicondaExtension.class, "miniconda", DefaultMinicondaExtension.class, project);
    }

    private void createConfigurations(Project project) {
        project.getConfigurations().create(MINICONDA_INSTALLER_CONFIGURATION_NAME);
        project.getConfigurations().create(CONDA_CONFIGURATION_NAME);
    }

    private void createRepository(Project project) {
        project.getRepositories().ivy(ivy -> {
            ivy.setUrl(IVY_REPO_URL);
            ivy.patternLayout(layout -> layout.artifact("[organisation]/[module]-[revision]-[classifier].[ext]"));
        });
    }

    private void addDependencies(Project project) {
        final MinicondaExtension minicondaExtension = project.getExtensions().getByType(MinicondaExtension.class);
        final Miniconda miniconda = minicondaExtension.getMiniconda();
        project.getConfigurations().getByName(MINICONDA_INSTALLER_CONFIGURATION_NAME)
        .getIncoming().beforeResolve(dependencies -> {
            if (dependencies.getDependencies().isEmpty()) {
                final DependencyHandler handler = project.getDependencies();
                final Map<String, String> map = new HashMap<>();
                map.put("group", "miniconda");
                map.put("name", miniconda.getName());
                map.put("version", miniconda.getVersion());
                ModuleDependency minicondaInstaller = (ModuleDependency) handler.add(MINICONDA_INSTALLER_CONFIGURATION_NAME, map);
                minicondaInstaller.artifact(artifact -> {
                    artifact.setName(miniconda.getName());
                    artifact.setType(miniconda.getExtension());
                    artifact.setClassifier(miniconda.getClassifier());
                    artifact.setExtension(miniconda.getExtension());
                });
            }
        });
    }
}
