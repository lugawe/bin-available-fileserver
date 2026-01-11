package template.quarkus.server.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.quarkus.scheduler.Scheduled;
import io.vertx.core.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import template.quarkus.common.Events;
import template.quarkus.common.ping.PingService;
import template.quarkus.common.ping.PingServiceRegistry;
import template.quarkus.common.util.Blocker;

@ApplicationScoped
public class ScheduledPingService {

    // TODO events for node up/down and master up/down

    private static final Logger log = LoggerFactory.getLogger(ScheduledPingService.class);

    @Inject
    private Blocker blocker;

    @Inject
    private EventBus eventBus;

    @Inject
    private PingServiceRegistry pingServiceRegistry;

    public ScheduledPingService() {}

    protected boolean isMain(String nodeId) {
        // TODO
        return false;
    }

    @Scheduled(every = "3s")
    public void scheduledPinging() {
        // TODO message los
        PingService.PingPackage pingPackage = new PingService.PingPackage("ping");
        pingServiceRegistry.getAllRegisteredMap().forEach((k, v) -> {
            try {
                v.ping(pingPackage);
                eventBus.publish(Events.NODE_NAME, Events.NODE_UP + k);
            } catch (Exception e) {
                eventBus.publish(Events.NODE_NAME, Events.NODE_DOWN + k);
                if (isMain(k)) {
                    eventBus.publish(Events.ELECTION_NAME, k);
                }
            }
        });
    }
}
