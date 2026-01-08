package template.quarkus.server;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import io.quarkus.scheduler.Scheduled;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import template.quarkus.common.sync.SyncFileServiceRegistry;

@Singleton
public class NodeConfig {

    private static final Logger log = LoggerFactory.getLogger(NodeConfig.class);

    @ConfigProperty(name = "node.id")
    private String nodeId;

    @Inject
    private SyncFileServiceRegistry syncFileServiceRegistry;

    public NodeConfig() {}

    private void inContext(Runnable r) {
        try (MDC.MDCCloseable _ = MDC.putCloseable("node_id", nodeId)) {
            r.run();
        }
    }

    @Scheduled(every = "30s", delay = 5, delayUnit = TimeUnit.SECONDS)
    public void maybeDisable() {
        double v = ThreadLocalRandom.current().nextDouble();
        inContext(() -> {
            if (v < 0.9) {
                boolean die = v < 0.15;
                setDisabled(die);
            } else {
                setEnabled();
            }
        });
    }

    public void setDisabled(boolean die) {
        if (die) {
            log.error("Node {} is DEAD! Shutting down...", nodeId);
            Runtime.getRuntime().halt(1);
        } else {
            log.error("Node {} is down", nodeId);
        }
    }

    public void setEnabled() {
        log.info("Node {} is back", nodeId);
    }

    public String getNodeId() {
        return nodeId;
    }
}
