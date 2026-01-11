package template.quarkus.server.service;

import java.util.*;

import jakarta.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import template.quarkus.common.content.FileEntry;
import template.quarkus.common.content.FileVersionEntry;
import template.quarkus.common.content.UpdatePackage;

@ApplicationScoped
public class Storage {

    private static final Logger log = LoggerFactory.getLogger(Storage.class);

    private final Map<String, FileVersionEntry> store = new HashMap<>();
    private int latestVersion = 1;

    public Storage() {}

    public int replicaUpdate(UpdatePackage message) {
        if (message.getUntilVersion() <= latestVersion) return -2;
        if (message.getAfterVersion() - latestVersion != 0) return latestVersion;
        message.getFiles().forEach(fileVersionEntry -> store.put(fileVersionEntry.name(), fileVersionEntry));
        latestVersion = message.getUntilVersion();
        return 0;
    }

    public boolean writeClientFile(FileEntry fileEntry) {
        latestVersion++;
        store.put(fileEntry.name(), new FileVersionEntry(fileEntry.name(), fileEntry.bytes(), latestVersion));
        return false;
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
        return store.get(name).bytes();
    }
}
