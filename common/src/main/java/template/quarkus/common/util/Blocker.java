package template.quarkus.common.util;

import java.util.function.Supplier;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkus.vertx.ConsumeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import template.quarkus.common.Events;

@ApplicationScoped
public class Blocker {

    private static final Logger log = LoggerFactory.getLogger(Blocker.class);

    private boolean enabled = true;

    public Blocker() {}

    @ConsumeEvent(Events.ALIVE_NAME)
    public void consume(String value) {
        if (Events.ALIVE_DOWN.equals(value)) {
            enabled = false;
        } else if (Events.ALIVE_UP.equals(value)) {
            enabled = true;
        }
    }

    public void blockIfDown(Runnable runnable) {
        blockIfDown(() -> {
            runnable.run();
            return null;
        });
    }

    public <T> T blockIfDown(Supplier<T> supplier) {
        if (enabled) {
            return supplier.get();
        }
        while (!enabled) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                log.error("Interrupted", e);
            }
        }
        throw new RuntimeException();
    }
}
