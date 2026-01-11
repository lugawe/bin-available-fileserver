package template.quarkus.server.service;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.quarkus.scheduler.Scheduled;
import io.vertx.core.eventbus.EventBus;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import template.quarkus.common.Events;

@ApplicationScoped
public class ScheduledChaosService {

    private static final Logger log = LoggerFactory.getLogger(ScheduledChaosService.class);

    @Inject
    private EventBus eventBus;

    @ConfigProperty(name = "node.id")
    private String nodeId;

    public ScheduledChaosService() {}

    private void inContext(Runnable r) {
        try (MDC.MDCCloseable c = MDC.putCloseable("node_id", nodeId)) {
            r.run();
        }
    }

    @Scheduled(every = "20s", delay = 10, delayUnit = TimeUnit.SECONDS)
    public void maybeDisable() {
        double v = ThreadLocalRandom.current().nextDouble();
        inContext(() -> {
            if (v < 0.9) {
                boolean die = v < 0.15;
                setDisabled(die);
            } else {
                setEnabled();
            }
        });
    }

    public void setDisabled(boolean die) {
        if (die) {
            log.error("I am DEAD! Shutting down...");
            Runtime.getRuntime().halt(1); // Instant shutdown
        } else {
            log.error("I am down");
            eventBus.publish(Events.ALIVE_NAME, Events.ALIVE_DOWN);
        }
    }

    public void setEnabled() {
        log.info("Node {} is back", nodeId);
        eventBus.publish(Events.ALIVE_NAME, Events.ALIVE_UP);
    }
}
