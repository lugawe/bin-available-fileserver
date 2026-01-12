package template.quarkus.server.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.quarkus.vertx.ConsumeEvent;

import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.print.DocFlavor.STRING;

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

    public String myRole(){
        if(isMain())return "[Main]";
        else return "[Replika]";
    }

    @ConsumeEvent(value = Events.NODE_DOWN, blocking = true)
    public void onNodeDown(String nodeId) {
        new Thread(() -> {
            if (mainNodeId.equals(nodeId)) {
            log.info("{}: The current main node is not reachable", myRole());
            electionServiceRegistry.getAllAliveRegistered(nodeStateService.getActiveNodes().stream()
            .filter(s -> !s.contains(mainNodeId))
            .filter(s -> !s.contains(localNodeId))
            .collect(Collectors.toSet()))
            .forEach(es -> {
                Response response = es.requestToBecomeMain(new Request(localNodeId, storageService.getLatestVersion()));
                if(response.ok()){
                    mainNodeId = localNodeId;
                    log.info("{}: getMain erfolgreich", myRole());
                    storageService.writeSync();
                }else{
                    mainNodeId = response.NodeId();
                    log.info("{}: getMain nicht erfolgreich: {} ist jezt main", myRole(), mainNodeId);
                }
            });;
        }
        }).run(); 
    }

    public Response respondToElection(Request request){
        log.info("{}: got election Request from Node {}", myRole(), request.nodeId());
        if(request.maxVersion() > storageService.getLatestVersion()){
            mainNodeId = request.nodeId();
            log.info("{}: Accepted {} as new main", myRole(), request.nodeId());
            return new Response(localNodeId, true);
        }else if(request.maxVersion() == storageService.getLatestVersion()){
            mainNodeId = woMainWhenEqualVersion(request.nodeId());
            if(isMain()){
                storageService.writeSync();
                log.info("{}: I am new main", myRole());
                return new Response(localNodeId, false);
            }else{
                log.info("{}: Accepted {} as new main", myRole(), request.nodeId());
                return new Response(localNodeId, true);
            }
        }else{
            mainNodeId = localNodeId;
            log.info("{}: I am new main", myRole());
            storageService.writeSync();
            return new Response(localNodeId, false);
        }
    }

    private String woMainWhenEqualVersion(String otherNodeId){
        if(localNodeId.equals("node-1")){
            return "node-1";
        }else if(localNodeId.equals("node-2")){
            if(otherNodeId.equals("node-1"))return "node-1";
            else return "node-2";
        }else if(localNodeId.equals("node-3")){
            return otherNodeId;
        }
        return localNodeId;
    }

    public String getLocalNodeId(){
        return localNodeId;
    }

    public void setMain(String nodeId){
        mainNodeId = nodeId;
    }
}
