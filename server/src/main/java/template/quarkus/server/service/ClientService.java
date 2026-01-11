package template.quarkus.server.service;

import java.net.URI;
import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import template.quarkus.common.ClientFileRESTService;

@ApplicationScoped
public class ClientService {

    private String endpoint = "localhost";

    public ClientService() {}

    public ClientFileRESTService fileService(String nodeId) {
        return RestClientBuilder.newBuilder()
                .baseUri(URI.create("http://" + nodeId + ":3000/api"))
                .build(ClientFileRESTService.class);
    }

    public ClientFileRESTService fileService() {
        return fileService(endpoint);
    }

    public void setEndpoint(String nodeId){
        endpoint = nodeId;
    }
}