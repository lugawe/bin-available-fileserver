package template.quarkus.common.sync;

import template.quarkus.common.content.ChangeEntry;

// Used internal by Servers
public interface SyncFileService {

    void sync(ChangeEntry changeEntry);
}
