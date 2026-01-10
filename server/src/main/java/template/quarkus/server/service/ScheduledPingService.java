package template.quarkus.server.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.quarkus.scheduler.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private PingServiceRegistry pingServiceRegistry;

    public ScheduledPingService() {}

    @Scheduled(every = "3s")
    public void scheduledPinging() {
        blocker.maybeSendMessage(() -> {
            PingService.PingPackage pingPackage = new PingService.PingPackage("ping");
            pingServiceRegistry.getAllRegisteredMap().forEach((k, v) -> {
                try {
                    v.ping(pingPackage);
                } catch (Exception e) {
                    log.warn("Node {} is not available", k);
                }
            });
        });
    }
}
