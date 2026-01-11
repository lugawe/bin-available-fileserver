package template.quarkus.common.sync;

import template.quarkus.common.content.UpdateEntry;

// Used internal by Servers
public interface SyncFileService {

    int sync(UpdateEntry updateEntry);
}
