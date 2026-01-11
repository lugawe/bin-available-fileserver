package template.quarkus.server.service;

import java.util.*;

import jakarta.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import template.quarkus.common.content.ChangeEntry;
import template.quarkus.common.content.FileEntry;

@ApplicationScoped
public class Storage {

    private static final Logger log = LoggerFactory.getLogger(Storage.class);

    private final SortedSet<ChangeEntry> changeEntries =
            new TreeSet<>(Comparator.comparingInt(ChangeEntry::version).reversed());

    public Storage() {}

    public void write(ChangeEntry changeEntry) {
        log.info("Write");
        changeEntries.add(changeEntry);
    }

    public byte[] read(String file) {
        log.info("Read");
        for (ChangeEntry entry : changeEntries) {
            Optional<FileEntry> fileEntry =
                    entry.files().stream().filter(e -> e.name().equals(file)).findFirst();
            if (fileEntry.isPresent()) {
                return fileEntry.get().bytes();
            }
        }
        return null;
    }
}
