package template.quarkus.server.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.vertx.ConsumeEvent;
import template.quarkus.common.content.FileEntry;
import template.quarkus.common.content.UpdateEntry;
import template.quarkus.common.sync.SyncFileService;
import template.quarkus.common.sync.SyncFileServiceRegistry;
import template.quarkus.common.util.Blocker;
import template.quarkus.common.Events;

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

    public int store(UpdateEntry updateEntry) {
        return storage.writeReplicaUpdate(updateEntry);
    }

    public void writeThrough(FileEntry fileEntry) {
        boolean noChangesInFile = storage.writeClientFile(fileEntry);
        if (noChangesInFile) return;
        writeSync();
    }
    
    @ConsumeEvent(Events.NODE_UP)
    private void writeSync(String node) {
        syncHelper(syncFileServiceRegistry.getRegistered(node));
    }

    private void writeSync() {
        syncFileServiceRegistry.getAllRegistered().parallelStream().forEach(fs -> {
            syncHelper(fs);
        });
    }

    private void syncHelper(SyncFileService fs) {
        UpdateEntry nodePackage = new UpdateEntry(
                storage.getLatestVersion(), storage.getFilesChangedAfterVersion(storage.getLatestVersion() - 1));
        int returnStatusCode;
        do {
            returnStatusCode = fs.sync(nodePackage);
            if (returnStatusCode > 0)
                nodePackage = new UpdateEntry(
                        storage.getLatestVersion(),
                        returnStatusCode,
                        storage.getFilesChangedAfterVersion(returnStatusCode));
        } while (returnStatusCode != 0);
    }

    public byte[] read(String file) {
        return storage.read(file);
    }
}
