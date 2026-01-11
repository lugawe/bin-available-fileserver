package template.quarkus.server.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import template.quarkus.common.content.ChangeEntry;
import template.quarkus.common.content.FileEntry;
import template.quarkus.common.sync.SyncFileServiceRegistry;
import template.quarkus.common.util.Blocker;

@ApplicationScoped
public class StorageService {

    private static final Logger log = LoggerFactory.getLogger(StorageService.class);

    @Inject
    private Blocker blocker;

    @Inject
    private Storage storage;

    @Inject
    private SyncFileServiceRegistry syncFileServiceRegistry;

    public StorageService() {}

    public void syncLocal(ChangeEntry changeEntry) {
        storage.write(changeEntry);
    }

    public void syncToReplicas(FileEntry fileEntry) {
        storage.write(fileEntry);

        syncFileServiceRegistry.getAllRegistered().parallelStream()
                .forEach(fs -> blocker.maybeSendMessage(() -> fs.sync(changeEntry)));
    }

    public byte[] read(String file) {
        return storage.read(file);
    }
}
