package io.logbee.gradle.conda.conda.actions;

public class InstallDependencyException extends RuntimeException {

    public InstallDependencyException(String message) {
        super(message);
    }

    public InstallDependencyException(String message, Throwable cause) {
        super(message, cause);
    }
}
