package template.quarkus.common.endpoint;

import java.net.URI;

import jakarta.inject.Singleton;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

@Singleton
public class EndpointServiceRegistry {

    @ConfigProperty(name = "client.address")
    private String clientAddress;

    public EndpointServiceRegistry() {}

    private EndpointRESTService createEndpointRESTService(String nodeId) {
        return RestClientBuilder.newBuilder()
                .baseUri(URI.create("http://" + nodeId + ":9000/api"))
                .build(EndpointRESTService.class);
    }

    public EndpointRESTService createEndpointRESTService() {
        return createEndpointRESTService(clientAddress);
    }
}
