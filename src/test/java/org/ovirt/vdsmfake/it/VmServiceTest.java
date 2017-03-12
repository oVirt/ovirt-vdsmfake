package org.ovirt.vdsmfake.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.ovirt.vdsmfake.it.IntegrationTest.deployAll;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ovirt.vdsmfake.ContextHolder;
import org.ovirt.vdsmfake.service.VMService;

@RunWith(Arquillian.class)
public class VmServiceTest {

    @Inject
    private VMService vmService;

    @Deployment(name = "VMServiceTest")
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
        assertNotNull(vmService);
    }

    @Test
    public void runVm() {
        HashMap vmParams = new HashMap();
        vmParams.put("vmId", UUID.randomUUID().toString());
        vmParams.put("vmName", "vm1");
        vmParams.put("cpuName", "kabyLake");
        vmParams.put("memSize", "1024");

        Map createResult = vmService.create(vmParams);

        Object resultStatus = createResult.get("status");
        Map<String, String> vmList = (Map<String, String>) createResult.get("vmList");

        assertEquals(vmService.getDoneStatus().get("status"), resultStatus);
        assertEquals("WaitForLaunch", vmList.get("status"));
    }

}
