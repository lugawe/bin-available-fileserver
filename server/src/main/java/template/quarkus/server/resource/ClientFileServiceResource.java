package template.quarkus.server.resource;

import jakarta.inject.Inject;

import template.quarkus.common.ClientFileRESTService;
import template.quarkus.common.content.ChangeEntry;
import template.quarkus.common.content.FileEntry;
import template.quarkus.common.util.Blocker;
import template.quarkus.server.service.FileService;

public class ClientFileServiceResource implements ClientFileRESTService {

    @Inject
    private Blocker blocker;

    @Inject
    private FileService fileService;

    public ClientFileServiceResource() {}

    @Override
    public void write(ChangeEntry changeEntry) {
        blocker.blockIfDown(() -> {
            //
            fileService.writeThrough(changeEntry);
        });
    }

    @Override
    public FileEntry read(String file) {
        return blocker.blockIfDown(() -> {
            //
            return new FileEntry(file, fileService.read(file));
        });
    }
}
