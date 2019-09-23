package io.logbee.gradle.conda.conda;

public class Miniconda {

    private final String name;
    private final String version;
    private final String extension;
    private final String classifier;

    public Miniconda(String name, String version, String extension, String classifier) {
        this.name = name;
        this.version = version;
        this.extension = extension;
        this.classifier = classifier;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getExtension() {
        return extension;
    }

    public String getClassifier() {
        return classifier;
    }
}
