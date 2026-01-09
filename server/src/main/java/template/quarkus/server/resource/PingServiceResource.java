package template.quarkus.server.resource;

import jakarta.inject.Inject;

import template.quarkus.common.ping.PingRESTService;
import template.quarkus.common.util.Blocker;

public class PingServiceResource implements PingRESTService {

    @Inject
    private Blocker blocker;

    public PingServiceResource() {}

    @Override
    public PingPackage ping(PingPackage ping) {
        return blocker.blockIfDown(() -> {
            //
            return new PingPackage("pong");
        });
    }
}
