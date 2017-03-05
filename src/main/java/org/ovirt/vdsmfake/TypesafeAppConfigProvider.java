package org.ovirt.vdsmfake;

import javax.enterprise.inject.Produces;

import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;


public class TypesafeAppConfigProvider {

    @Produces
    public AppConfig producer() {
        return ConfigBeanFactory.create(ConfigFactory.load(), AppConfig.class);
    }

}
