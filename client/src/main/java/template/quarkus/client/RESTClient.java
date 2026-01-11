package template.quarkus.client;

import java.net.URI;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import template.quarkus.common.ClientFileRESTService;

@ApplicationScoped
public class RESTClient {

    @ConfigProperty(name = "client.endpoint")
    private String endpoint;

    public RESTClient() {}

    public ClientFileRESTService fileService(String nodeId) {
        return RestClientBuilder.newBuilder()
                .baseUri(URI.create("http://" + nodeId + ":8080/api"))
                .build(ClientFileRESTService.class);
    }

    public ClientFileRESTService fileService() {
        return fileService(endpoint);
    }
}
