package template.quarkus.common.ping;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/ping")
public interface PingRESTService extends PingService {

    @Override
    @POST
    @Path("/")
    PingPackage ping(PingPackage ping);
}
