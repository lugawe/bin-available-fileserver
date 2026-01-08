package template.quarkus.common.sync;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class SyncFileServiceRegistry {

    private static final Logger log = LoggerFactory.getLogger(SyncFileServiceRegistry.class);

    private static SyncFileService createFileService(String nodeId) {
        SyncFileRESTService syncFileService = RestClientBuilder.newBuilder()
                .baseUri(URI.create("http://" + nodeId + ":8080/api"))
                .build(SyncFileRESTService.class);
        return new ChaosSyncFileService(syncFileService);
    }

    private final Map<String, SyncFileService> cache = new ConcurrentHashMap<>();

    @ConfigProperty(name = "node.replicas")
    private List<String> replicas;

    public SyncFileServiceRegistry() {}

    @PostConstruct
    public void init() {
        for (String replica : replicas) {
            log.info("Create Sync REST Client for {}", replica);
            add(replica);
        }
    }

    public SyncFileService add(String nodeId) {
        return cache.computeIfAbsent(nodeId, SyncFileServiceRegistry::createFileService);
    }

    public Collection<SyncFileService> getAllRegistered() {
        return Collections.unmodifiableCollection(cache.values());
    }
}
