package template.quarkus.server.resource;

import io.quarkus.vertx.ConsumeEvent;
import template.quarkus.common.Events;
import template.quarkus.common.ping.PingRESTService;

public class PingServiceResource implements PingRESTService {

    private boolean enabled = true;

    public PingServiceResource() {}

    @ConsumeEvent(Events.ALIVE_NAME)
    public void consume(String value) {
        if (Events.ALIVE_DOWN.equals(value)) {
            setEnabled(false);
        } else if (Events.ALIVE_UP.equals(value)) {
            setEnabled(true);
        }
    }

    @Override
    public PingPackage ping(PingPackage ping) {
        if (enabled) {
            return new PingPackage("pong");
        }
        throw new RuntimeException("TODO");
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
