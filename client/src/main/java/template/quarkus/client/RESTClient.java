package template.quarkus.client;

import java.net.URI;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import io.quarkus.vertx.ConsumeEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import template.quarkus.common.ClientFileRESTService;
import template.quarkus.common.Events;

@ApplicationScoped
public class RESTClient {

    private static final Logger log = LoggerFactory.getLogger(RESTClient.class);

    @ConfigProperty(name = "client.endpoint")
    private String endpoint;

    private String currentEndpoint;

    public RESTClient() {}

    @PostConstruct
    public void init() {
        currentEndpoint = endpoint;
    }

    @ConsumeEvent(Events.NEW_ENDPOINT)
    public void onNewEndpoint(String newNodeId) {
        log.info("update endpoint to {}", newNodeId);
        currentEndpoint = newNodeId;
    }

    public ClientFileRESTService fileService(String nodeId) {
        return RestClientBuilder.newBuilder()
                .baseUri(URI.create("http://" + nodeId + ":8081/api"))
                .build(ClientFileRESTService.class);
    }

    public ClientFileRESTService fileService() {
        return fileService(currentEndpoint);
    }
}
