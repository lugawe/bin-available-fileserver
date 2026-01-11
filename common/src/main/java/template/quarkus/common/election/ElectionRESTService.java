package template.quarkus.common.election;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/election")
public interface ElectionRESTService extends ElectionService {

    @Override
    @POST
    @Path("/")
    Response requestToBecomeMain(Request request);
}
