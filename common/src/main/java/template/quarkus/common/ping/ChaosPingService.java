package template.quarkus.common.ping;

import template.quarkus.common.util.Blocker;

public class ChaosPingService implements PingService {

    private final Blocker blocker;
    private final PingService pingService;

    public ChaosPingService(Blocker blocker, PingService pingService) {
        this.blocker = blocker;
        this.pingService = pingService;
    }

    @Override
    public PingPackage ping(PingPackage ping) {
        return blocker.blockIfDown(() -> pingService.ping(ping));
    }
}
