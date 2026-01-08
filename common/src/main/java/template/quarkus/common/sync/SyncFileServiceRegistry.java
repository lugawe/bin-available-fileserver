package template.quarkus.common.sync;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.inject.Singleton;

import org.eclipse.microprofile.rest.client.RestClientBuilder;

@Singleton
public class SyncFileServiceRegistry {

    private static SyncFileService createFileService(String nodeId) {
        SyncFileRESTService syncFileService = RestClientBuilder.newBuilder()
                .baseUri(URI.create("http://" + nodeId + ":8080/api"))
                .build(SyncFileRESTService.class);
        return new ChaosSyncFileService(syncFileService);
    }

    private final Map<String, SyncFileService> cache = new ConcurrentHashMap<>();

    public SyncFileServiceRegistry() {}

    public SyncFileService add(String nodeId) {
        return cache.computeIfAbsent(nodeId, SyncFileServiceRegistry::createFileService);
    }

    public Collection<SyncFileService> getAllRegistered() {
        return Collections.unmodifiableCollection(cache.values());
    }
}
