package io.logbee.gradle.conda.python.internal;

import io.logbee.gradle.conda.python.PythonPluginExtension;
import org.gradle.api.Project;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.SourceSetContainer;

public class DefaultPythonPluginExtension implements PythonPluginExtension {

    private final ProjectInternal project;
    private final ObjectFactory objectFactory;

    public DefaultPythonPluginExtension(Project project, ObjectFactory objectFactory) {
        this.project = (ProjectInternal) project;
        this.objectFactory = objectFactory;
    }

    @Override
    public ProjectInternal getProject() {
        return project;
    }

    @Override
    public SourceSetContainer getSourceSets() {
        return project.getExtensions().getByType(SourceSetContainer.class);
    }
}
