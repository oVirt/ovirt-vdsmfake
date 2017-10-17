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

@Path("hosts")
public class HostsResource {

    @Inject
    private VdsmManager vdsmManager;

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> hosts() {
        Map<String, Object> map = new HashMap<>();
        map.put("hosts", vdsmManager.getAllhosts());
        return map;
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> hosts(@PathParam("id") String id) {
        Map<String, Object> map = new HashMap<>();
        if (id != null) {
            map.compute(
                    "hosts",
                    (k, v) ->
                    vdsmManager.getAllhosts()
                            .stream()
                            .filter(host -> host.getId().equals(id))
                            .findFirst().get()
            );
        }
        return map;
    }
}
