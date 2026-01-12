package template.quarkus.server.service;

import java.util.*;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.quarkus.scheduler.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import template.quarkus.common.content.FileEntry;
import template.quarkus.common.content.FileVersionEntry;
import template.quarkus.common.content.UpdateEntry;

@ApplicationScoped
public class Storage {

    private static final Logger log = LoggerFactory.getLogger(Storage.class);

    private final Map<String, FileVersionEntry> store = new HashMap<>();
    private int latestVersion = 1;

    @Inject
    ElectionService electionService;

    public Storage() {}

    public int writeReplicaUpdate(UpdateEntry message) {
        if (message.untilVersion() < latestVersion) {
            log.error("Jüngere version: schwerer Fehler");
            return -2;
        }
        if (message.afterVersion() - latestVersion != 0) {
            log.info("{}: localstorage benötigt Files ab Version {} nicht {}", electionService.myRole(), latestVersion, message.afterVersion());
            return latestVersion;
        }
        message.files().forEach(fileVersionEntry -> store.put(fileVersionEntry.name(), fileVersionEntry));
        latestVersion = message.untilVersion();
        log.info("{}: Files in der versionsrange ]{},{}] wurden gespeichert", electionService.myRole(), message.afterVersion(), message.untilVersion());
        printFileList();
        return 0;
    }

    public void writeClientFile(FileEntry fileEntry) {
        latestVersion++;
        store.put(fileEntry.name(), new FileVersionEntry(fileEntry.name(), fileEntry.bytes(), latestVersion));
    }

    public FileVersionEntry getFileEntry(String filename) {
        return store.get(filename);
    }

    public int getVersion(String filename) {
        return store.get(filename).version();
    }

    public int getLatestVersion() {
        return latestVersion;
    }

    public List<FileVersionEntry> getFilesChangedAfterVersion(int version) {
        List<FileVersionEntry> changedAfterVersion = new ArrayList<>();
        store.forEach((key, value) -> {
            if (value.version() > version) changedAfterVersion.add(value);
        });
        return changedAfterVersion;
    }

    public byte[] read(String name) {
        if (!store.containsKey(name)) {
            return null;
        }
        return store.get(name).bytes();
    }

    @Scheduled(every = "30s")
    public void printFileList() {
        log.info("{}: File-List (V{})", electionService.myRole(), latestVersion);
        store.forEach((key, value) -> {
            log.info("File: {}; Version: {}", electionService.myRole(), key, value.version());
        });
    }
}
