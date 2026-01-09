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

    private final Object lock = new Object();

    private boolean enabled = true;

    public Blocker() {}

    @ConsumeEvent(Events.ALIVE_NAME)
    public void consume(String value) {
        synchronized (lock) {
            if (Events.ALIVE_DOWN.equals(value)) {
                enabled = false;
            } else if (Events.ALIVE_UP.equals(value)) {
                enabled = true;
                lock.notifyAll();
            }
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
}
