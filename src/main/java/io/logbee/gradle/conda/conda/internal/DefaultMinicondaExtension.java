package io.logbee.gradle.conda.conda.internal;

import io.logbee.gradle.conda.conda.Miniconda;
import io.logbee.gradle.conda.conda.MinicondaExtension;
import org.gradle.api.Project;
import org.gradle.internal.os.OperatingSystem;

import java.io.File;

public class DefaultMinicondaExtension implements MinicondaExtension {

    private static final OperatingSystem OS = OperatingSystem.current();

    private final Project project;

    private String version;
    private File baseDir;

    public DefaultMinicondaExtension(Project project) {
        this.project = project;
        this.baseDir = new File(project.getGradle().getGradleUserHomeDir(), "miniconda");
        this.version = "4.7.10";
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public File getBaseDir() {
        return baseDir;
    }

    @Override
    public void setBaseDir(File dir) {
        this.baseDir = dir;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public File getInstallationDir() {
        return new File(getBaseDir(), getName() + "-" + getVersion());
    }

    @Override
    public File getCondaExecutable() {
        return new File(getInstallationDir(), "bin/conda");
    }

    @Override
    public Miniconda getMiniconda() {
        return new Miniconda(getName(), getVersion(), getExtension(), getOsName() + "-" + getArch());
    }

    private String getName() {
        return "Miniconda3";
    }

    private String getOsName() {
        if (OS.isWindows()) return "Windows";
        else if (OS.isMacOsX()) return "MacOSX";
        else if (OS.isLinux()) return "Linux";
        else throw new IllegalArgumentException("Supported operating systems are: Windows, MacOSX, Linux");
    }

    private String getExtension() {
        if (OS.isWindows()) return "exe";
        else return "sh";
    }

    private String getArch() {
        return "x86_64";
    }
}
