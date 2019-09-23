package io.logbee.gradle.conda.conda;

import org.gradle.api.Project;

import java.io.File;

public interface MinicondaExtension {

    Project getProject();

    File getBaseDir();

    void setBaseDir(File dir);

    String getVersion();

    void setVersion(String version);

    File getInstallationDir();

    File getCondaExecutable();

    Miniconda getMiniconda();
}
