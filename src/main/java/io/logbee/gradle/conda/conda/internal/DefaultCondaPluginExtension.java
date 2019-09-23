package io.logbee.gradle.conda.conda.internal;

import io.logbee.gradle.conda.conda.CondaPluginExtension;
import io.logbee.gradle.conda.conda.MinicondaExtension;
import org.gradle.api.Project;
import org.gradle.api.internal.project.ProjectInternal;

import java.io.File;

public class DefaultCondaPluginExtension implements CondaPluginExtension {

    private final ProjectInternal project;

    private File condaDir;

    public DefaultCondaPluginExtension(ProjectInternal project) {
        this.project = project;
        this.condaDir = new File(project.getProjectDir(), ".gradle/conda");
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public File getEnvironmentDir() {
        return condaDir;
    }

    @Override
    public void setEnvironmentDir(File dir) {
        this.condaDir = dir;
    }

    @Override
    public MinicondaExtension getMiniconda() {
        return project.getExtensions().getByType(MinicondaExtension.class);
    }
}
