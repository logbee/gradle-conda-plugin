package io.logbee.gradle.conda.conda;

import org.gradle.api.Project;

import java.io.File;

public interface CondaPluginExtension {

    Project getProject();

    File getEnvironmentDir();

    void setEnvironmentDir(File dir);

    MinicondaExtension getMiniconda();
}
