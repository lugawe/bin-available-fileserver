package template.quarkus.server.resource;

import jakarta.inject.Inject;

import template.quarkus.common.ClientFileRESTService;
import template.quarkus.common.FileContent;
import template.quarkus.common.UpdatePackage;
import template.quarkus.common.util.Blocker;
import template.quarkus.server.service.FileService;

public class ClientFileServiceResource implements ClientFileRESTService {

    @Inject
    private Blocker blocker;

    @Inject
    private FileService fileService;

    public ClientFileServiceResource() {}

    @Override
    public void write(UpdatePackage updatePackage) {
        blocker.blockIfDown(() -> {
            //
            fileService.writeThrough(updatePackage);
        });
    }

    @Override
    public FileContent read(String file) {
        return blocker.blockIfDown(() -> {
            //
            return new FileContent(fileService.read(file));
        });
    }
}
