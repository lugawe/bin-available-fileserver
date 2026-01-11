package template.quarkus.common.sync;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import template.quarkus.common.content.ChangeEntry;

// Used internal by Servers
@Path("/sync")
public interface SyncFileRESTService extends SyncFileService {

    @Override
    @POST
    @Path("/")
    void sync(ChangeEntry changeEntry);
}
