package template.quarkus.common.sync;

import template.quarkus.common.content.UpdatePackage;

// Used internal by Servers
public interface SyncFileService {

    int sync(UpdatePackage updatePackage);
}
