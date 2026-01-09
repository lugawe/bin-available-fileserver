package template.quarkus.server.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import template.quarkus.server.service.FailoverNotifier;

@Path("/test")
@jakarta.enterprise.context.ApplicationScoped
public class FailoverTestResource {

    @Inject
    FailoverNotifier failoverNotifier;

    @POST
    @Path("/force-failover")
    public void forceFailover() {
        // Test: 8081 becomes new leader
        failoverNotifier.notifyClients("http://localhost:8081");
    }
}
