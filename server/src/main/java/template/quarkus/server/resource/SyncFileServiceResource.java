package template.quarkus.server.resource;

import jakarta.inject.Inject;

import io.quarkus.vertx.ConsumeEvent;
import template.quarkus.common.Events;
import template.quarkus.common.UpdatePackage;
import template.quarkus.common.sync.SyncFileRESTService;
import template.quarkus.server.service.FileService;

public class SyncFileServiceResource implements SyncFileRESTService {

    @Inject
    private FileService fileService;

    private boolean enabled = true;

    public SyncFileServiceResource() {}

    @ConsumeEvent(Events.ALIVE_NAME)
    public void consume(String value) {
        if (Events.ALIVE_DOWN.equals(value)) {
            setEnabled(false);
        } else if (Events.ALIVE_UP.equals(value)) {
            setEnabled(true);
        }
    }

    @Override
    public void sync(UpdatePackage updatePackage) {
        if (enabled) {
            fileService.store(updatePackage);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
