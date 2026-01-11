package template.quarkus.client;

import jakarta.inject.Inject;

import template.quarkus.common.ClientRESTService;


public class ClientUpdateResource implements ClientRESTService {


    public ClientUpdateResource() {}

    @Inject
    private RESTClient restClient;

	@Override
	public int newAdress(String nodeId) {
        restClient.setEndpoint(nodeId);
        return 0;
	}
}
