package template.quarkus.client;

import jakarta.inject.Inject;

import io.vertx.core.eventbus.EventBus;
import template.quarkus.common.Events;
import template.quarkus.common.endpoint.EndpointRESTService;

public class EndpointResource implements EndpointRESTService {

    @Inject
    private EventBus eventBus;

    public EndpointResource() {}

    @Override
    public void updateEndpoint(Request request) {
        eventBus.publish(Events.NEW_ENDPOINT, request.newNodeId());
    }
}
