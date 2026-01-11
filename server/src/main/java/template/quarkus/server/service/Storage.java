package template.quarkus.server.service;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import template.quarkus.common.content.ChangeEntry;

@ApplicationScoped
public class Storage {

    private static final Logger log = LoggerFactory.getLogger(Storage.class);

    private final List<ChangeEntry> files = new ArrayList<>();

    public Storage() {}

    public void write(ChangeEntry changeEntry) {
        files.add(changeEntry);
        log.info("Write");
    }

    public byte[] read(String file) {
        // byte[] bytes = store.get(file);
        log.info("Read");
        return null;
    }
}
