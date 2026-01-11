package template.quarkus.common.election;

import template.quarkus.common.util.Blocker;

public class ChaosElectionService implements ElectionService {

    private final Blocker blocker;
    private final ElectionService electionService;

    public ChaosElectionService(Blocker blocker, ElectionService electionService) {
        this.blocker = blocker;
        this.electionService = electionService;
    }

    @Override
    public Response requestToBecomeMain(Request request) {
        return blocker.blockIfDown(() -> electionService.requestToBecomeMain(request));
    }
}
