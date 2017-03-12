package org.ovirt.vdsmfake.it;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ovirt.vdsmfake.it.IntegrationTest.deployAll;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ovirt.vdsmfake.ContextHolder;
import org.ovirt.vdsmfake.service.HostService;

@RunWith(Arquillian.class)
public class HostServiceTest {

    @Inject
    private HostService hostService;

    @Deployment(name = "HostServiceTest")
    public static JavaArchive deploy() {
        return deployAll();
    }

    @Before
    public void before() {
        ContextHolder.init();
        ContextHolder.setServerName("localhost-arquillian");
    }

    @Test
    public void sanity() {
        assertNotNull(hostService);
    }

    @Test
    public void getCaps() {
        assertTrue(hostService.getVdsCapabilities().values().size() > 0);
    }

    @Test
    public void getStats() {
        assertTrue(hostService.getVdsStats().values().size() > 0);
    }
}
