package template.quarkus.common.ping;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import template.quarkus.common.util.Blocker;

@Singleton
public class PingServiceRegistry {

    private static final Logger log = LoggerFactory.getLogger(PingServiceRegistry.class);

    private final Map<String, ChaosPingService> cache = new ConcurrentHashMap<>();

    @Inject
    private Blocker blocker;

    @ConfigProperty(name = "node.replicas")
    private List<String> replicas;

    public PingServiceRegistry() {}

    private ChaosPingService createPingService(String nodeId) {
        PingRESTService pingRESTService = RestClientBuilder.newBuilder()
                .baseUri(URI.create("http://" + nodeId + ":8080/api"))
                .build(PingRESTService.class);
        return new ChaosPingService(blocker, pingRESTService);
    }

    @PostConstruct
    public void init() {
        for (String replica : replicas) {
            log.info("Create Ping REST Client for {}", replica);
            add(replica);
        }
    }

    public PingService add(String nodeId) {
        return cache.computeIfAbsent(nodeId, this::createPingService);
    }

    public Collection<PingService> getAllRegistered() {
        return Collections.unmodifiableCollection(cache.values());
    }

    public Map<String, PingService> getAllRegisteredMap() {
        return Collections.unmodifiableMap(cache);
    }
}
