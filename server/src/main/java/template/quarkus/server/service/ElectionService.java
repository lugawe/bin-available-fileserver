package template.quarkus.server.service;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkus.vertx.ConsumeEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import template.quarkus.common.Events;

@ApplicationScoped
public class ElectionService {

    @ConfigProperty(name = "node.main")
    private String mainNodeId;

    public ElectionService() {}

    @ConsumeEvent(Events.NODE_DOWN)
    public void onNodeDown(String nodeId) {
        if (mainNodeId.equals(nodeId)) {
            // election
        }
    }
}
