package template.quarkus.server.resource;

import jakarta.inject.Inject;

import template.quarkus.common.UpdatePackage;
import template.quarkus.common.sync.SyncFileRESTService;
import template.quarkus.server.service.FileService;

public class SyncFileServiceResource implements SyncFileRESTService {

    @Inject
    private FileService fileService;

    public SyncFileServiceResource() {}

    @Override
    public void sync(UpdatePackage updatePackage) {
        fileService.store(updatePackage);
    }
}
