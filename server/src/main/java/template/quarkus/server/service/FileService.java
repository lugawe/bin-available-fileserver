package template.quarkus.server.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import template.quarkus.common.content.ChangeEntry;
import template.quarkus.common.sync.SyncFileServiceRegistry;
import template.quarkus.common.util.Blocker;

@ApplicationScoped
public class FileService {

    private static final Logger log = LoggerFactory.getLogger(FileService.class);

    @Inject
    private Blocker blocker;

    @Inject
    private FileStorage fileStorage;

    @Inject
    private SyncFileServiceRegistry syncFileServiceRegistry;

    public FileService() {}

    public void store(ChangeEntry changeEntry) {
        fileStorage.write(changeEntry);
    }

    public void writeThrough(ChangeEntry changeEntry) {
        fileStorage.write(changeEntry);

        syncFileServiceRegistry.getAllRegistered().parallelStream()
                .forEach(fs -> blocker.maybeSendMessage(() -> fs.sync(changeEntry)));
    }

    public byte[] read(String file) {
        return fileStorage.read(file);
    }
}
