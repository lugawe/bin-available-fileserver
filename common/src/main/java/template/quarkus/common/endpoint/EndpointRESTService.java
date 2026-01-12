package template.quarkus.common.endpoint;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/endpoint")
public interface EndpointRESTService extends EndpointService {

    @Override
    @POST
    @Path("/")
    void updateEndpoint(Request request);
}
