package template.quarkus.server.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.quarkus.scheduler.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import template.quarkus.common.ping.PingService;
import template.quarkus.common.ping.PingServiceRegistry;

@ApplicationScoped
public class ScheduledPingService {

    private static final Logger log = LoggerFactory.getLogger(ScheduledPingService.class);

    @Inject
    private PingServiceRegistry pingServiceRegistry;

    public ScheduledPingService() {}

    @Scheduled(every = "3s")
    public void scheduledPinging() {
        PingService.PingPackage pingPackage = new PingService.PingPackage("ping");
        pingServiceRegistry.getAllRegisteredMap().forEach((k, v) -> {
            try {
                PingService.PingPackage p = v.ping(pingPackage);
                log.info("Node {} is available: {}", k, p);
                // TODO Besseres wann ist Node nicht verfügbar
            } catch (Exception e) {
                log.warn("Node {} is not available", k);
            }
        });
    }
}
