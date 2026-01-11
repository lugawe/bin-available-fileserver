package template.quarkus.server.service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.quarkus.scheduler.Scheduled;
import io.vertx.core.eventbus.EventBus;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import template.quarkus.common.Events;
import template.quarkus.common.ping.PingService;
import template.quarkus.common.ping.PingServiceRegistry;

@ApplicationScoped
public class NodeStateService {

    private static final Logger log = LoggerFactory.getLogger(NodeStateService.class);

    private final Set<String> activeNodes = ConcurrentHashMap.newKeySet();

    @Inject
    private ManagedExecutor executor;

    @Inject
    private EventBus eventBus;

    @Inject
    private PingServiceRegistry pingServiceRegistry;

    @ConfigProperty(name = "node.replicas")
    private List<String> replicas;

    public NodeStateService() {}

    @PostConstruct
    public void init() {
        activeNodes.addAll(replicas);
    }

    @Scheduled(every = "2s", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    public void ping() {

        // TODO message loss

        List<CompletableFuture<Void>> futures = new ArrayList<>(2);

        for (Map.Entry<String, PingService> entry :
                pingServiceRegistry.getAllRegisteredMap().entrySet()) {

            String nodeId = entry.getKey();
            PingService pingService = entry.getValue();

            CompletableFuture<Void> f = CompletableFuture.runAsync(() -> pingNode(nodeId, pingService), executor);

            futures.add(f);
        }

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
    }

    private void pingNode(String nodeId, PingService pingService) {
        PingService.PingPackage pingPackage = new PingService.PingPackage("ping");
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
    }

    public Set<String> getActiveNodes() {
        return Collections.unmodifiableSet(activeNodes);
    }
}
