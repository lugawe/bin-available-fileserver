package template.quarkus.server.resource;

import jakarta.inject.Inject;

import template.quarkus.common.election.ElectionRESTService;
import template.quarkus.common.content.FileEntry;
import template.quarkus.common.util.Blocker;
import template.quarkus.server.service.ElectionService;
import template.quarkus.server.service.StorageService;

public class ElectionServiceResource implements ElectionRESTService {

    @Inject
    private Blocker blocker;

    @Inject
    private ElectionService electionService;

    public ElectionServiceResource() {}

	@Override
	public Response requestToBecomeMain(Request request) {
		return blocker.blockIfDown(() -> {
            //
            return electionService.respondToElection(request);
        });
	}
}
