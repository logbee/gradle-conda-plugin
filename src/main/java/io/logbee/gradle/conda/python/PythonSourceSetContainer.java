package io.logbee.gradle.conda.python;

import org.gradle.api.internal.AbstractNamedDomainObjectContainer;
import org.gradle.api.internal.CollectionCallbackActionDecorator;
import org.gradle.internal.reflect.Instantiator;

public abstract class PythonSourceSetContainer extends AbstractNamedDomainObjectContainer<PythonSourceSet> {

    public PythonSourceSetContainer(Instantiator instantiator, CollectionCallbackActionDecorator callbackDecorator) {
        super(PythonSourceSet.class, instantiator, PythonSourceSet::getName, callbackDecorator);
    }
}
