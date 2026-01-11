package template.quarkus.server.resource;

import jakarta.inject.Inject;

import template.quarkus.common.ClientFileRESTService;
import template.quarkus.common.content.ChangeEntry;
import template.quarkus.common.content.FileEntry;
import template.quarkus.common.util.Blocker;
import template.quarkus.server.service.StorageService;

public class ClientFileServiceResource implements ClientFileRESTService {

    @Inject
    private Blocker blocker;

    @Inject
    private StorageService storageService;

    public ClientFileServiceResource() {}

    @Override
    public void write(FileEntry fileEntry) {
        blocker.blockIfDown(() -> {
            //
            storageService.writeThrough(fileEntry);
        });
    }

    @Override
    public FileEntry read(String file) {
        return blocker.blockIfDown(() -> {
            //
            return new FileEntry(file, storageService.read(file));
        });
    }
}
