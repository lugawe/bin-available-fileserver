package template.quarkus.common;

import jakarta.ws.rs.*;

import template.quarkus.common.content.FileEntry;

// Used by Client
@Path("/client")
public interface ClientRESTService {

    @POST
    @Path("/")
    int newAdress(String nodeId);
}