package org.ovirt.vdsmfake.boundry;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.ovirt.vdsmfake.domain.VdsmManager;

@Path("")
public class VdsmFakeResource {

    @Inject
    private VdsmManager vdsmManager;

    @GET
    @Path("stats")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> stats() {
        Map<String, Object> map = new HashMap<>();
        map.put("hostsCount", vdsmManager.getHostCount());
        map.put("vmsCount", vdsmManager.getRunningVmsCount());
        map.put("dataCenters", vdsmManager.getAllStoragePools());
        return map;
    }

}
