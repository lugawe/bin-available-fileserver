package template.quarkus.common.util;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkus.vertx.ConsumeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import template.quarkus.common.Events;

@ApplicationScoped
public class Blocker {

    private static final Logger log = LoggerFactory.getLogger(Blocker.class);

    private final Object lock = new Object();

    private boolean enabled = true;

    public Blocker() {}

    @ConsumeEvent(Events.ALIVE_DOWN)
    public void consumeAliveDown(String s) {
        synchronized (lock) {
            enabled = false;
        }
    }

    @ConsumeEvent(Events.ALIVE_UP)
    public void consumeAliveUp(String s) {
        synchronized (lock) {
            enabled = true;
            lock.notifyAll();
        }
    }

    public void blockIfDown(Runnable runnable) {
        blockIfDown(() -> {
            runnable.run();
            return null;
        });
    }

    public <T> T blockIfDown(Supplier<T> supplier) {
        synchronized (lock) {
            while (!enabled) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Interrupted while waiting", e);
                }
            }
        }

        return supplier.get();
    }

    public void maybeSendMessage(Runnable r) {
        double v = ThreadLocalRandom.current().nextDouble();
        if (v < 0.9) {
            r.run();
        } else {
            log.error("Failed to send message... < 90%");
        }
    }

    public boolean isEnabled() {
        return enabled;
    }
}
