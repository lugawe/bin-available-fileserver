package template.quarkus.server.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.quarkus.vertx.ConsumeEvent;

import java.util.stream.Collectors;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import template.quarkus.common.Events;
import template.quarkus.common.election.ElectionService.Request;
import template.quarkus.common.election.ElectionService.Response;
import template.quarkus.common.election.ElectionServiceRegistry;

@ApplicationScoped
public class ElectionService {

    private static final Logger log = LoggerFactory.getLogger(ElectionService.class);

    @ConfigProperty(name = "node.id")
    private String localNodeId;

    @ConfigProperty(name = "node.main")
    private String mainNodeId;

    @Inject
    private ElectionServiceRegistry electionServiceRegistry;

    @Inject 
    private NodeStateService nodeStateService;

    @Inject
    private StorageService storageService;
    
    public ElectionService() {}

    public boolean isMain() {
        return localNodeId.equals(mainNodeId);
    }

    @ConsumeEvent(value = Events.NODE_DOWN, blocking = true)
    public void onNodeDown(String nodeId) {
        new Thread(() -> {
            if (mainNodeId.equals(nodeId)) {
            log.info("The current main node is not reachable");
            electionServiceRegistry.getAllAliveRegistered(nodeStateService.getActiveNodes().stream()
            .filter(s -> !s.contains(mainNodeId))
            .filter(s -> !s.contains(localNodeId))
            .collect(Collectors.toSet()))
            .forEach(es -> {
                Response response = es.requestToBecomeMain(new Request(localNodeId, storageService.getLatestVersion()));
                if(response.ok()){
                    log.info("getMain erfolgreich");
                    mainNodeId = localNodeId;
                    storageService.writeSync();
                }else{
                    mainNodeId = response.NodeId();
                    log.info("getMain erfolgreich nicht erfolgreich: {} ist jezt main", mainNodeId);
                }
            });;
        }
        }).run(); 
    }

    public Response respondToElection(Request request){
        if(request.maxVersion() >= storageService.getLatestVersion()){
            mainNodeId = request.nodeId();
            return new Response(localNodeId, true);
        }else{
            mainNodeId = localNodeId;
            storageService.writeSync();
            return new Response(localNodeId, false);
        }
    }
}
