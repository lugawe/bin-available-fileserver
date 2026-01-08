package template.quarkus.server.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FileStorage {

    private final Map<String, byte[]> store = new ConcurrentHashMap<>();

    public FileStorage() {}

    public void write(Map<String, byte[]> files) {
        store.putAll(files);
    }

    public void write(String file, byte[] content) {
        store.put(file, content);
    }

    public byte[] read(String file) {
        return store.get(file);
    }
}
