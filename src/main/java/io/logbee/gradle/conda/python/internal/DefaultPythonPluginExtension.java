package io.logbee.gradle.conda.python.internal;

import groovy.lang.Closure;
import io.logbee.gradle.conda.python.PythonPluginExtension;
import org.gradle.api.Project;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.model.ObjectFactory;

public class DefaultPythonPluginExtension implements PythonPluginExtension {

    private final ProjectInternal project;
    private final ObjectFactory objectFactory;
    private final DefaultPythonSourceSetContainer sourceSets;

    public DefaultPythonPluginExtension(Project project, ObjectFactory objectFactory) {
        this.project = (ProjectInternal) project;
        this.objectFactory = objectFactory;
        this.sourceSets = objectFactory.newInstance(DefaultPythonSourceSetContainer.class);
    }

    @Override
    public ProjectInternal getProject() {
        return project;
    }

    @Override
    public DefaultPythonSourceSetContainer getSourceSets() {
        return sourceSets;
    }

    @Override
    public Object sourceSets(Closure closure) {
        return sourceSets.configure(closure);
    }
}
