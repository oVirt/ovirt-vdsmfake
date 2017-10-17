package org.ovirt.vdsmfake.boundry;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.ovirt.vdsmfake.domain.VdsmManager;

@Path("datacenters")
public class DataCentersResource {

    @Inject
    private VdsmManager vdsmManager;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> dataCenters(@PathParam("id") String id) {
        Map<String, Object> map = new HashMap<>();
        if (id == null) {
            map.put("dataCenters", vdsmManager.getAllStoragePools());
        } else {
            map.put(
                    "dataCenters",
                    vdsmManager.getAllStoragePools()
                            .stream()
                            .filter(host -> host.getId().equals(id))
                            .findFirst().get()
            );
        }
        return map;
    }
}
