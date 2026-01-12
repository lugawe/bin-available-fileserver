package template.quarkus.server.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;

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

    @Inject 
    private NodeStateService nodeStateService;

    @Inject
    private ElectionService electionService;

    public StorageService() {}

    public int store(UpdateEntry updateEntry) {
        electionService.setMain(updateEntry.nodeId());
        return storage.writeReplicaUpdate(updateEntry);
    }

    public void writeThrough(FileEntry fileEntry) {
        if(electionService.isMain()){
            storage.writeClientFile(fileEntry);
            log.info("{}: File {} written to storage",electionService.myRole(), fileEntry.name());
            writeSync();
        }else{
            log.error("got request from client, but am not main");
        }
    }
    
    @ConsumeEvent(value = Events.NODE_UP, blocking = true) 
    public void writeSync(String node) {
        if (electionService.isMain()) {
            log.info("{}: Resync of node {}",electionService.myRole(), node);
            new Thread(() -> syncHelper(syncFileServiceRegistry.getRegistered(node))).run(); 
        }
    }

    public void writeSync() {
        if (electionService.isMain()) {
            nodeStateService.getActiveNodes().forEach(an -> {
                log.info("{}: Synchronizing version {} to {}", electionService.myRole(), storage.getLatestVersion(), an);
                syncHelper(syncFileServiceRegistry.getRegistered(an));
            });
        }else{
            log.info("i am not main");
        }
    }

    private void syncHelper(SyncFileService fs) {
        UpdateEntry nodePackage;
        if(storage.getLatestVersion()==1){
            nodePackage = new UpdateEntry(1, 1, new ArrayList<>(), electionService.getLocalNodeId());
        }else{
            nodePackage = new UpdateEntry(
                storage.getLatestVersion(), storage.getFilesChangedAfterVersion(storage.getLatestVersion() - 1), electionService.getLocalNodeId());
        }
         
        int returnStatusCode;
        do {
            log.info("{}: Sending sync for version range ]{},{}]", electionService.myRole(), nodePackage.afterVersion(),nodePackage.untilVersion());
            returnStatusCode = fs.sync(nodePackage);
            if (returnStatusCode > 0){
                nodePackage = new UpdateEntry(
                        storage.getLatestVersion(),
                        returnStatusCode,
                        storage.getFilesChangedAfterVersion(returnStatusCode),
                        electionService.getLocalNodeId());
                        log.info("{}: got request to send sync for version range ]{},{}]", electionService.myRole(), nodePackage.afterVersion(),nodePackage.untilVersion());
            }

        } while (returnStatusCode != 0);
    }

    public byte[] read(String file) {
        return storage.read(file);
    }

    public int getLatestVersion(){
        return storage.getLatestVersion();
    }
}
