package template.quarkus.common.sync;

import template.quarkus.common.UpdatePackage;

// Used internal by Servers
public interface SyncFileService {

    void sync(UpdatePackage updatePackage);
}
