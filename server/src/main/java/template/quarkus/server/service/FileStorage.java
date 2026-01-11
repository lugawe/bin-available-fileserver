package template.quarkus.server.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import template.quarkus.common.content.ChangeEntry;

@ApplicationScoped
public class FileStorage {

    private static final Logger log = LoggerFactory.getLogger(FileStorage.class);

    private final Map<String, byte[]> store = new ConcurrentHashMap<>();

    public FileStorage() {}

    public void write(ChangeEntry changeEntry) {
        changeEntry.files().forEach(e -> write(e.name(), e.bytes()));
    }

    public void write(String file, byte[] content) {
        store.put(file, content);
        log.info("Stored file: {}", file);
    }

    public byte[] read(String file) {
        byte[] bytes = store.get(file);
        log.info("Read file: {}", file);
        return bytes;
    }
}
