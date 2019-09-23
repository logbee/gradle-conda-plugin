package io.logbee.gradle.conda.python.internal;

import org.gradle.api.Action;
import org.gradle.api.artifacts.ComponentMetadataSupplierDetails;
import org.gradle.api.artifacts.component.ComponentArtifactIdentifier;
import org.gradle.api.artifacts.component.ModuleComponentIdentifier;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.artifacts.repositories.RepositoryContentDescriptor;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.ConfiguredModuleComponentRepository;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.ModuleComponentRepositoryAccess;
import org.gradle.api.internal.artifacts.ivyservice.resolveengine.artifact.ResolvableArtifact;
import org.gradle.api.internal.artifacts.repositories.ResolutionAwareRepository;
import org.gradle.api.internal.artifacts.repositories.descriptor.FlatDirRepositoryDescriptor;
import org.gradle.api.internal.artifacts.repositories.descriptor.RepositoryDescriptor;
import org.gradle.api.internal.artifacts.repositories.resolver.MetadataFetchingCost;
import org.gradle.api.internal.component.ArtifactType;
import org.gradle.internal.action.InstantiatingAction;
import org.gradle.internal.component.external.model.ModuleDependencyMetadata;
import org.gradle.internal.component.model.ComponentArtifactMetadata;
import org.gradle.internal.component.model.ComponentOverrideMetadata;
import org.gradle.internal.component.model.ComponentResolveMetadata;
import org.gradle.internal.component.model.ModuleSource;
import org.gradle.internal.resolve.result.*;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CondaArtifactRepository implements ArtifactRepository, ResolutionAwareRepository {

    public CondaArtifactRepository() {
    }

    @Override
    public String getName() {
        return "conda";
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException("Name cannot be changed!");
    }

    @Override
    public void content(Action<? super RepositoryContentDescriptor> configureAction) {
        throw new UnsupportedOperationException("CondaArtifactRepository.content");
    }

    @Override
    public ConfiguredModuleComponentRepository createResolver() {
        return new CondaArtifactRepository.CondaRepository();
    }

    //https://anaconda.org/conda-forge/pytest/5.1.2/download/linux-ppc64le/pytest-5.1.2-py36_0.tar.bz2

    @Override
    public RepositoryDescriptor getDescriptor() {
//        return new IvyRepositoryDescriptor.Builder("conda", URI.create("https://anaconda.org/conda-forge/"))
//                .setLayoutType("pattern")
//                .setIvyPatterns(Collections.emptyList())
//                .setArtifactPatterns(Collections.emptyList())
//                .setM2Compatible(false)
//                .setMetadataSources(Collections.emptyList())
//                .setAuthenticationSchemes(Collections.emptyList())
//                .setAuthenticated(false)
//                .create();
        return new FlatDirRepositoryDescriptor(getName(), Collections.emptyList());
    }

    private class CondaRepository implements ConfiguredModuleComponentRepository {

        private final Map<ComponentArtifactIdentifier, ResolvableArtifact> cache = new HashMap<>();

        @Override
        public boolean isDynamicResolveMode() {
            return false;
        }

        @Override
        public boolean isLocal() {
            return false;
        }

        @Override
        public String getId() {
            return "conda";
        }

        @Override
        public String getName() {
            return "conda";
        }

        @Override
        public ModuleComponentRepositoryAccess getLocalAccess() {
            return null;
        }

        @Override
        public ModuleComponentRepositoryAccess getRemoteAccess() {

            return new ModuleComponentRepositoryAccess() {

                @Override
                public void listModuleVersions(ModuleDependencyMetadata dependency, BuildableModuleVersionListingResolveResult result) {
                    throw new UnsupportedOperationException("ModuleComponentRepositoryAccess.listModuleVersions");
                }

                @Override
                public void resolveComponentMetaData(ModuleComponentIdentifier moduleComponentIdentifier, ComponentOverrideMetadata requestMetaData, BuildableModuleComponentMetaDataResolveResult result) {
                    throw new UnsupportedOperationException("ModuleComponentRepositoryAccess.resolveComponentMetaData");
                }

                @Override
                public void resolveArtifacts(ComponentResolveMetadata component, BuildableComponentArtifactsResolveResult result) {
                    throw new UnsupportedOperationException("ModuleComponentRepositoryAccess.resolveArtifacts");
                }

                @Override
                public void resolveArtifactsWithType(ComponentResolveMetadata component, ArtifactType artifactType, BuildableArtifactSetResolveResult result) {
                    throw new UnsupportedOperationException("ModuleComponentRepositoryAccess.resolveArtifactsWithType");
                }

                @Override
                public void resolveArtifact(ComponentArtifactMetadata artifact, ModuleSource moduleSource, BuildableArtifactResolveResult result) {
                    throw new UnsupportedOperationException("ModuleComponentRepositoryAccess.resolveArtifact");
                }

                @Override
                public MetadataFetchingCost estimateMetadataFetchingCost(ModuleComponentIdentifier moduleComponentIdentifier) {
                    return MetadataFetchingCost.EXPENSIVE;
                }
            };
        }

        @Override
        public Map<ComponentArtifactIdentifier, ResolvableArtifact> getArtifactCache() {
            return cache;
        }

        @Nullable
        @Override
        public InstantiatingAction<ComponentMetadataSupplierDetails> getComponentMetadataSupplier() {
            return null;
        }
    }
}
