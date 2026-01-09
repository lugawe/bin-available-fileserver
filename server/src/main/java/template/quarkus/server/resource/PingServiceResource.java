package template.quarkus.server.resource;

import template.quarkus.common.ping.PingRESTService;

public class PingServiceResource implements PingRESTService {

    public PingServiceResource() {}

    @Override
    public PingPackage ping(PingPackage ping) {
        return new PingPackage("pong");
    }
}
