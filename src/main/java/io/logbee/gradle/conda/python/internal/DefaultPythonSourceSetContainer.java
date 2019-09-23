package io.logbee.gradle.conda.python.internal;

import io.logbee.gradle.conda.python.PythonSourceSet;
import io.logbee.gradle.conda.python.PythonSourceSetContainer;
import org.gradle.api.Namer;
import org.gradle.api.internal.AbstractNamedDomainObjectContainer;
import org.gradle.api.internal.CollectionCallbackActionDecorator;
import org.gradle.api.model.ObjectFactory;
import org.gradle.internal.reflect.Instantiator;

import javax.inject.Inject;

public class DefaultPythonSourceSetContainer extends PythonSourceSetContainer {

    private final ObjectFactory objectFactory;

    @Inject
    public DefaultPythonSourceSetContainer(Instantiator instantiator, ObjectFactory objectFactory, CollectionCallbackActionDecorator callbackDecorator) {
        super(instantiator, callbackDecorator);
        this.objectFactory = objectFactory;
    }

    @Override
    protected PythonSourceSet doCreate(String name) {
        return new DefaultPythonSourceSet(name, objectFactory);
    }
}
