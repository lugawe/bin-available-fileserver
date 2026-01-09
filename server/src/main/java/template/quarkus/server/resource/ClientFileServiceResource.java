package template.quarkus.server.resource;

import jakarta.inject.Inject;

import template.quarkus.common.ClientFileService;
import template.quarkus.common.FileContent;
import template.quarkus.common.UpdatePackage;
import template.quarkus.server.service.ClientRegistry;
import template.quarkus.server.service.FileService;

public class ClientFileServiceResource implements ClientFileService {

    @Inject
    private FileService fileService;

    @Inject
    private ClientRegistry clientRegistry;

    public ClientFileServiceResource() {}

    @Override
    public void write(UpdatePackage updatePackage) {
        // Auto-register client on first call
        // registerClientIfNeeded(updatePackage);

        fileService.writeThrough(updatePackage);
    }

    @Override
    public FileContent read(String file) {
        // Auto-register client on first call
        // registerClientIfNeeded(updatePackage);

        return new FileContent(fileService.read(file));
    }


    private void registerClientIfNeeded(UpdatePackage updatePackage) {
//        String clientId = updatePackage.getClientId();
//        String clientIp = updatePackage.getClientIp();
//        int clientPort = updatePackage.getClientPort();
//
//        if (!clientRegistry.getClients().containsKey(clientId)) {
//            clientRegistry.registerClient(clientId, clientIp, clientPort);
//        }
    }

}
