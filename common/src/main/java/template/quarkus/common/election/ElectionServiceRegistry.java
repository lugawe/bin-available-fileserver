package template.quarkus.common.election;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import template.quarkus.common.sync.SyncFileService;
import template.quarkus.common.util.Blocker;

@Singleton
public class ElectionServiceRegistry {

    private static final Logger log = LoggerFactory.getLogger(ElectionServiceRegistry.class);

    private final Map<String, ChaosElectionService> cache = new ConcurrentHashMap<>();

    @Inject
    private Blocker blocker;

    @ConfigProperty(name = "node.replicas")
    private List<String> replicas;

    public ElectionServiceRegistry() {}

    private ChaosElectionService createElectionService(String nodeId) {
        ElectionRESTService electionRESTService = RestClientBuilder.newBuilder()
                .baseUri(URI.create("http://" + nodeId + ":8080/api"))
                .build(ElectionRESTService.class);
        return new ChaosElectionService(blocker, electionRESTService);
    }

    @PostConstruct
    public void init() {
        for (String replica : replicas) {
            log.info("Create Election REST Client for {}", replica);
            add(replica);
        }
    }

    public ElectionService add(String nodeId) {
        return cache.computeIfAbsent(nodeId, this::createElectionService);
    }

    public Collection<ElectionService> getAllRegistered() {
        return Collections.unmodifiableCollection(cache.values());
    }

    public Collection<ElectionService> getAllAliveRegistered(Set<String> alive) {
        Collection<ElectionService> aliveNodes = new ArrayList<>();
        cache.keySet().forEach(key -> {if(alive.contains(key)) aliveNodes.add(cache.get(key));});
        return aliveNodes;
    }

    public Map<String, ElectionService> getAllRegisteredMap() {
        return Collections.unmodifiableMap(cache);
    }

    public ElectionService getRegistered(String nodeId) {
        return cache.get(nodeId);
    }
}
