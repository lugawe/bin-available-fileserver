package template.quarkus.common.ping;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.inject.Singleton;

import org.eclipse.microprofile.rest.client.RestClientBuilder;

@Singleton
public class PingServiceRegistry {

    private static PingService createPingService(String nodeId) {
        PingRESTService pingRESTService = RestClientBuilder.newBuilder()
                .baseUri(URI.create("http://" + nodeId + ":8080/api"))
                .build(PingRESTService.class);
        return new ChaosPingService(pingRESTService);
    }

    private final Map<String, PingService> cache = new ConcurrentHashMap<>();

    public PingServiceRegistry() {}

    public PingService add(String nodeId) {
        return cache.computeIfAbsent(nodeId, PingServiceRegistry::createPingService);
    }

    public Collection<PingService> getAllRegistered() {
        return Collections.unmodifiableCollection(cache.values());
    }
}
