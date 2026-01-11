package template.quarkus.common.sync;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import io.quarkus.vertx.ConsumeEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import template.quarkus.common.Events;
import template.quarkus.common.util.Blocker;

@Singleton
public class SyncFileServiceRegistry {

    private static final Logger log = LoggerFactory.getLogger(SyncFileServiceRegistry.class);

    private final Map<String, ChaosSyncFileService> cache = new ConcurrentHashMap<>();

    @Inject
    private Blocker blocker;

    @ConfigProperty(name = "node.replicas")
    private List<String> replicas;

    public SyncFileServiceRegistry() {}

    private ChaosSyncFileService createFileService(String nodeId) {
        SyncFileRESTService syncFileService = RestClientBuilder.newBuilder()
                .baseUri(URI.create("http://" + nodeId + ":8080/api"))
                .build(SyncFileRESTService.class);
        return new ChaosSyncFileService(blocker, syncFileService);
    }

    @PostConstruct
    public void init() {
        for (String replica : replicas) {
            log.info("Create Sync REST Client for {}", replica);
            add(replica);
        }
    }

    @ConsumeEvent(Events.NODE_DOWN)
    public void onNodeDown(String nodeId) {
        cache.remove(nodeId);
    }

    @ConsumeEvent(Events.NODE_UP)
    public void onNodeUp(String nodeId) {
        add(nodeId);
    }

    public SyncFileService add(String nodeId) {
        return cache.computeIfAbsent(nodeId, this::createFileService);
    }

    public Collection<SyncFileService> getAllRegistered() {
        return Collections.unmodifiableCollection(cache.values());
    }

    public SyncFileService getRegistered(String nodeId) {
        return cache.get(nodeId);
    }
}
