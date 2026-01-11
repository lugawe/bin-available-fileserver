package template.quarkus.server.resource;

import jakarta.inject.Inject;

import template.quarkus.common.content.UpdatePackage;
import template.quarkus.common.sync.SyncFileRESTService;
import template.quarkus.common.util.Blocker;
import template.quarkus.server.service.StorageService;

public class SyncFileServiceResource implements SyncFileRESTService {

    @Inject
    private Blocker blocker;

    @Inject
    private StorageService storageService;

    public SyncFileServiceResource() {}

    @Override
    public int sync(UpdatePackage updatePackage) {

        return storageService.store(updatePackage);
    }
}
