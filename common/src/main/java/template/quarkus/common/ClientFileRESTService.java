package template.quarkus.common;

import jakarta.ws.rs.*;

// Used by Client
@Path("/files")
public interface ClientFileRESTService {

    @POST
    @Path("/")
    void write(UpdatePackage updatePackage);

    @GET
    @Path("/{file}")
    FileContent read(@PathParam("file") String file);
}
