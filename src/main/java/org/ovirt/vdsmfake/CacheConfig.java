package org.ovirt.vdsmfake;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;

public class CacheConfig {

    @Inject
    private AppConfig appConfig;

    @Produces
    public Configuration greetingCacheConfiguration() {
        return new ConfigurationBuilder()
                .persistence()
                .addSingleFileStore().location(appConfig.getCacheDir() + "/objectStore")
                .maxEntries(-1)
                .async()
                .build();
    }

}
