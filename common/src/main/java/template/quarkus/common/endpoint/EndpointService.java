package template.quarkus.common.endpoint;

public interface EndpointService {

    record Request(String newNodeId) {}

    void updateEndpoint(Request request);
}
