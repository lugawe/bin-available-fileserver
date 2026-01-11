package template.quarkus.common.sync;

import template.quarkus.common.content.ChangeEntry;
import template.quarkus.common.util.Blocker;

public class ChaosSyncFileService implements SyncFileService {

    private final Blocker blocker;
    private final SyncFileService syncFileService;

    public ChaosSyncFileService(Blocker blocker, SyncFileService syncFileService) {
        this.blocker = blocker;
        this.syncFileService = syncFileService;
    }

    @Override
    public void sync(ChangeEntry changeEntry) {
        blocker.blockIfDown(() -> syncFileService.sync(changeEntry));
    }
}
