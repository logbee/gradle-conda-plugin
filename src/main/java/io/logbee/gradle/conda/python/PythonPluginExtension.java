package io.logbee.gradle.conda.python;

import groovy.lang.Closure;
import org.gradle.api.Project;

public interface PythonPluginExtension {

    Project getProject();

    PythonSourceSetContainer getSourceSets();

    Object sourceSets(Closure closure);
}
