package template.quarkus.common;

import jakarta.ws.rs.*;

import template.quarkus.common.content.ChangeEntry;
import template.quarkus.common.content.FileEntry;

// Used by Client
@Path("/files")
public interface ClientFileRESTService {

    @POST
    @Path("/")
    void write(ChangeEntry changeEntry);

    @GET
    @Path("/{file}")
    FileEntry read(@PathParam("file") String file);
}
