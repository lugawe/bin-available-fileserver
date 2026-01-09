package template.quarkus.common.sync;

import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import template.quarkus.common.UpdatePackage;

public class ChaosSyncFileService implements SyncFileService {

    private static final Logger log = LoggerFactory.getLogger(ChaosSyncFileService.class);

    private final SyncFileService syncFileService;

    public ChaosSyncFileService(SyncFileService syncFileService) {
        this.syncFileService = syncFileService;
    }

    public void setEnabled(boolean enabled) {}

    @Override
    public void sync(UpdatePackage updatePackage) {
        double v = ThreadLocalRandom.current().nextDouble();
        if (v < 0.9) {
            syncFileService.sync(updatePackage);
        } else {
            log.error("Failed to sync... < 90%");
        }
    }
}
