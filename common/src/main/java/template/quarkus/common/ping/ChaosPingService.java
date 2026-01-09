package template.quarkus.common.ping;

import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChaosPingService implements PingService {

    private static final Logger log = LoggerFactory.getLogger(ChaosPingService.class);

    private final PingService pingService;

    public ChaosPingService(PingService pingService) {
        this.pingService = pingService;
    }

    @Override
    public PingPackage ping(PingPackage ping) {
        double v = ThreadLocalRandom.current().nextDouble();
        if (v < 0.9) {
            return pingService.ping(ping);
        } else {
            log.error("Failed to sync... < 90%");
        }
        throw new RuntimeException("TODO");
    }
}
