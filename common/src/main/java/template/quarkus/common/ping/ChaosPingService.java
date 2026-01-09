package template.quarkus.common.ping;

import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import template.quarkus.common.util.Blocker;

public class ChaosPingService implements PingService {

    private static final Logger log = LoggerFactory.getLogger(ChaosPingService.class);

    private final Blocker blocker;
    private final PingService pingService;

    public ChaosPingService(Blocker blocker, PingService pingService) {
        this.blocker = blocker;
        this.pingService = pingService;
    }

    @Override
    public PingPackage ping(PingPackage ping) {
        blocker.blockIfDown();
        double v = ThreadLocalRandom.current().nextDouble();
        if (v < 0.9) {
            return pingService.ping(ping);
        } else {
            log.error("Failed to sync... < 90%");
            throw new RuntimeException();
        }
    }
}
