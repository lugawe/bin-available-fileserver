package template.quarkus.server.service;

import java.util.Collection;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import template.quarkus.common.SyncFileService;
import template.quarkus.common.UpdatePackage;

@ApplicationScoped
public class FileService {

    private static final Logger log = LoggerFactory.getLogger(FileService.class);

    @Inject
    private FileStorage fileStorage;

    @Inject
    private SyncFileServiceRegistry syncFileServiceRegistry;

    private Collection<SyncFileService> syncFileServiceReplicas;

    public FileService() {}

    @PostConstruct
    public void init() {
        syncFileServiceReplicas = syncFileServiceRegistry.getAllRegistered();
    }

    public void store(UpdatePackage updatePackage) {
        fileStorage.write(updatePackage.getFiles());
    }

    public void writeThrough(UpdatePackage updatePackage) {
        fileStorage.write(updatePackage.getFiles());
        syncFileServiceReplicas.parallelStream().forEach(fs -> fs.sync(updatePackage));
    }

    public byte[] read(String file) {
        return fileStorage.read(file);
    }
}
