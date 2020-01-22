package io.logbee.gradle.conda.python;

import groovy.lang.Closure;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSetContainer;

public interface PythonPluginExtension {

    Project getProject();

    SourceSetContainer getSourceSets();
}
