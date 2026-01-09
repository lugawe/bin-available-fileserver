package template.quarkus.server.resource;

import jakarta.inject.Inject;

import io.quarkus.vertx.ConsumeEvent;
import template.quarkus.common.ClientFileRESTService;
import template.quarkus.common.Events;
import template.quarkus.common.FileContent;
import template.quarkus.common.UpdatePackage;
import template.quarkus.server.service.FileService;

public class ClientFileServiceResource implements ClientFileRESTService {

    @Inject
    private FileService fileService;

    private boolean enabled;

    public ClientFileServiceResource() {}

    @ConsumeEvent(Events.ALIVE_NAME)
    public void consume(String value) {
        if (Events.ALIVE_DOWN.equals(value)) {
            setEnabled(false);
        } else if (Events.ALIVE_UP.equals(value)) {
            setEnabled(true);
        }
    }

    @Override
    public void write(UpdatePackage updatePackage) {
        if (enabled) {
            fileService.writeThrough(updatePackage);
        }
    }

    @Override
    public FileContent read(String file) {
        if (enabled) {
            return new FileContent(fileService.read(file));
        }
        throw new RuntimeException("TODO");
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
