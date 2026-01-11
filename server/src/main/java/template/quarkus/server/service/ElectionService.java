package template.quarkus.server.service;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkus.vertx.ConsumeEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import template.quarkus.common.Events;

@ApplicationScoped
public class ElectionService {

    private static final Logger log = LoggerFactory.getLogger(ElectionService.class);

    @ConfigProperty(name = "node.id")
    private String nodeId;

    @ConfigProperty(name = "node.main")
    private String mainNodeId;

    public ElectionService() {}

    public boolean isMain() {
        return nodeId.equals(mainNodeId);
    }

    @ConsumeEvent(Events.NODE_DOWN)
    public void onNodeDown(String nodeId) {
        if (mainNodeId.equals(nodeId)) {
            log.info("Needs election");
        }
    }
}
