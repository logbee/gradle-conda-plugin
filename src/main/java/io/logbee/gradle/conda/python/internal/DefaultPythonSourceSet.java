package io.logbee.gradle.conda.python.internal;

import groovy.lang.Closure;
import io.logbee.gradle.conda.python.PythonSourceSet;
import org.gradle.api.Action;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTreeElement;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.specs.Spec;

import java.io.File;

import static org.gradle.util.ConfigureUtil.configure;

public class DefaultPythonSourceSet implements PythonSourceSet {

    private final String name;
    private final ObjectFactory objectFactory;

    private final SourceDirectorySet pythonSource;
    private final SourceDirectorySet resources;

    public DefaultPythonSourceSet(String name, ObjectFactory objectFactory) {

        this.name = name;
        this.objectFactory = objectFactory;

        pythonSource = objectFactory.sourceDirectorySet("python", name + " python source");
        pythonSource.getFilter().include("**/*.py");

        resources = objectFactory.sourceDirectorySet("resources", name + " resources");
        resources.getFilter().exclude(new Spec<FileTreeElement>() {
            @Override
            public boolean isSatisfiedBy(FileTreeElement element) {
                return pythonSource.contains(element.getFile());
            }
        });
    }

    @Override public String getName() {
        return name;
    }

    @Override public SourceDirectorySet getPython() {
        return pythonSource;
    }

    @Override public SourceDirectorySet getResources() {
        return resources;
    }

    @Override
    public FileCollection getSources() {
        return pythonSource.getSourceDirectories().plus(resources.getSourceDirectories());
    }

    @Override public PythonSourceSet python(Closure<SourceDirectorySet> configureClosure) {
        configure(configureClosure, getPython());
        return this;
    }

    @Override public PythonSourceSet python(Action<? super SourceDirectorySet> configureAction) {
        configureAction.execute(getPython());
        return this;
    }

    @Override public PythonSourceSet resources(Closure<SourceDirectorySet> configureClosure) {
        configure(configureClosure, getResources());
        return this;
    }

    @Override public PythonSourceSet resources(Action<? super SourceDirectorySet> configureAction) {
        configureAction.execute(getResources());
        return this;
    }
}
