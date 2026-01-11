package template.quarkus.server.service;

import java.util.*;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.quarkus.scheduler.Scheduled;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.impl.ConcurrentHashSet;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import template.quarkus.common.Events;
import template.quarkus.common.ping.PingService;
import template.quarkus.common.ping.PingServiceRegistry;

@ApplicationScoped
public class NodeStateService {

    private static final Logger log = LoggerFactory.getLogger(NodeStateService.class);

    private final Set<String> activeNodes = new ConcurrentHashSet<>();

    @ConfigProperty(name = "node.replicas")
    private List<String> replicas;

    @Inject
    private EventBus eventBus;

    @Inject
    private PingServiceRegistry pingServiceRegistry;

    public NodeStateService() {}

    @PostConstruct
    public void init() {
        activeNodes.addAll(replicas);
    }

    @Scheduled(every = "3s")
    public void ping() {

        // TODO message loss

        PingService.PingPackage pingPackage = new PingService.PingPackage("ping");

        pingServiceRegistry.getAllRegisteredMap().entrySet().parallelStream().forEach((entry) -> {
            String nodeId = entry.getKey();
            PingService pingService = entry.getValue();

            try {
                pingService.ping(pingPackage);
                if (activeNodes.add(nodeId)) {
                    log.info("Another node is up: {}", nodeId);
                    eventBus.publish(Events.NODE_UP, nodeId);
                }
            } catch (Exception e) {
                if (activeNodes.remove(nodeId)) {
                    log.info("Another node is down: {}", nodeId);
                    eventBus.publish(Events.NODE_DOWN, nodeId);
                }
            }
        });
    }

    public Set<String> getActiveNodes() {
        return Collections.unmodifiableSet(activeNodes);
    }
}
