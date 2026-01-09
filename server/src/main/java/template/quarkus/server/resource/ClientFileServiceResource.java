package template.quarkus.server.resource;

import jakarta.inject.Inject;

import template.quarkus.common.ClientFileRESTService;
import template.quarkus.common.FileContent;
import template.quarkus.common.UpdatePackage;
import template.quarkus.server.service.FileService;

public class ClientFileServiceResource implements ClientFileRESTService {

    @Inject
    private FileService fileService;

    public ClientFileServiceResource() {}

    @Override
    public void write(UpdatePackage updatePackage) {
        fileService.writeThrough(updatePackage);
    }

    @Override
    public FileContent read(String file) {
        return new FileContent(fileService.read(file));
    }
}
