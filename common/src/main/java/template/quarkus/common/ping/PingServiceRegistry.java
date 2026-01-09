package template.quarkus.common.ping;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;

import io.quarkus.vertx.ConsumeEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import template.quarkus.common.Events;

@Singleton
public class PingServiceRegistry {

    private static final Logger log = LoggerFactory.getLogger(PingServiceRegistry.class);

    private static ChaosPingService createPingService(String nodeId) {
        PingRESTService pingRESTService = RestClientBuilder.newBuilder()
                .baseUri(URI.create("http://" + nodeId + ":8080/api"))
                .build(PingRESTService.class);
        return new ChaosPingService(pingRESTService);
    }

    private final Map<String, ChaosPingService> cache = new ConcurrentHashMap<>();

    @ConfigProperty(name = "node.replicas")
    private List<String> replicas;

    public PingServiceRegistry() {}

    @PostConstruct
    public void init() {
        for (String replica : replicas) {
            log.info("Create Ping REST Client for {}", replica);
            add(replica);
        }
    }

    @ConsumeEvent(Events.ALIVE_NAME)
    public void consume(String value) {
        if (Events.ALIVE_DOWN.equals(value)) {
            cache.forEach((k, v) -> v.setEnabled(false));
        } else if (Events.ALIVE_UP.equals(value)) {
            cache.forEach((k, v) -> v.setEnabled(true));
        }
    }

    public PingService add(String nodeId) {
        return cache.computeIfAbsent(nodeId, PingServiceRegistry::createPingService);
    }

    public Collection<PingService> getAllRegistered() {
        return Collections.unmodifiableCollection(cache.values());
    }

    public Map<String, PingService> getAllRegisteredMap() {
        return Collections.unmodifiableMap(cache);
    }
}
