package template.quarkus.server.resource;

import jakarta.inject.Inject;

import template.quarkus.common.content.ChangeEntry;
import template.quarkus.common.sync.SyncFileRESTService;
import template.quarkus.common.util.Blocker;
import template.quarkus.server.service.FileService;

public class SyncFileServiceResource implements SyncFileRESTService {

    @Inject
    private Blocker blocker;

    @Inject
    private FileService fileService;

    public SyncFileServiceResource() {}

    @Override
    public void sync(ChangeEntry changeEntry) {
        blocker.blockIfDown(() -> {
            //
            fileService.store(changeEntry);
        });
    }
}
