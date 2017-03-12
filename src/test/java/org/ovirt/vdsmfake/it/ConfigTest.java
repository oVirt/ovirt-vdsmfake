package org.ovirt.vdsmfake.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.ovirt.vdsmfake.it.IntegrationTest.shrinkWrap;
import static org.ovirt.vdsmfake.it.IntegrationTest.withConfigArtifacts;
import static org.ovirt.vdsmfake.it.IntegrationTest.withEmptyBeansXml;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ovirt.vdsmfake.AppConfig;

@RunWith(Arquillian.class)
public class ConfigTest {

    @Inject
    private AppConfig appConfig;

    @Deployment(name = "ConfigTest")
    public static JavaArchive deploy() {
        return withConfigArtifacts()
                .andThen(withEmptyBeansXml())
                .apply(shrinkWrap());
    }

    @Test
    public void sanity() {
        assertNotNull(appConfig);
    }

    @Test
    public void getArchitecture() {
        assertEquals(AppConfig.ArchitectureType.X86_64.name(), appConfig.getArchitectureType());
    }
}
