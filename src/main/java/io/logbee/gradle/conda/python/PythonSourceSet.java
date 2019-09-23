package io.logbee.gradle.conda.python;

import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.SourceDirectorySet;

public interface PythonSourceSet {

    String getName();

    SourceDirectorySet getPython();

    SourceDirectorySet getResources();

    FileCollection getSources();

    PythonSourceSet python(Closure<SourceDirectorySet> configureClosure);

    PythonSourceSet python(Action<? super SourceDirectorySet> configureAction);

    PythonSourceSet resources(Closure<SourceDirectorySet> configureClosure);

    PythonSourceSet resources(Action<? super SourceDirectorySet> configureAction);
}
