package template.quarkus.common.util;

import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkus.vertx.ConsumeEvent;
import template.quarkus.common.Events;

@ApplicationScoped
public class Blocker {

    private boolean enabled = true;

    public Blocker() {}

    @ConsumeEvent(Events.ALIVE_NAME)
    public void consume(String value) {
        if (Events.ALIVE_DOWN.equals(value)) {
            setEnabled(false);
        } else if (Events.ALIVE_UP.equals(value)) {
            setEnabled(true);
        }
    }

    public void blockIfDown() {}

    public void block(long amount, TimeUnit timeUnit) {
        try {
            Thread.sleep(timeUnit.toMillis(amount));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void block() {
        block(5, TimeUnit.SECONDS);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
