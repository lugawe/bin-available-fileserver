package template.quarkus.server.service;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.quarkus.scheduler.Scheduled;
import io.quarkus.vertx.ConsumeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.print.DocFlavor.STRING;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import template.quarkus.common.Events;
import template.quarkus.common.election.ElectionService.Request;
import template.quarkus.common.election.ElectionService.Response;
import template.quarkus.common.election.ElectionServiceRegistry;
import template.quarkus.common.endpoint.EndpointRESTService;
import template.quarkus.common.endpoint.EndpointService;
import template.quarkus.common.endpoint.EndpointServiceRegistry;

@ApplicationScoped
public class ElectionService {

    private static final Logger log = LoggerFactory.getLogger(ElectionService.class);

    @ConfigProperty(name = "node.id")
    private String localNodeId;

    private List<String> replicas = new ArrayList<>(Arrays.asList("tuxedo-laptop:8081","tuxedo-laptop:8082","tuxedo-laptop:8083"));
    
    @ConfigProperty(name = "node.main")
    private String mainNodeId;

    @Inject
    private ElectionServiceRegistry electionServiceRegistry;

    @Inject 
    private NodeStateService nodeStateService;

    @Inject
    private StorageService storageService;

    @Inject
    private EndpointServiceRegistry endpointServiceRegistry;

    private EndpointRESTService endpointRESTService;
    
    public ElectionService() {}

    @PostConstruct
    public void init() {
        endpointRESTService = endpointServiceRegistry.createEndpointRESTService();
    }

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
            while(!makeElectionRequest())log.info("election failed. retrying");
        }
        }).run(); 
    }
    @Scheduled(every = "300s", delay = 2, delayUnit = TimeUnit.SECONDS)
    public void updateMain(){
        while(!makeElectionRequest())log.info("election failed. retrying");
    }
    private boolean makeElectionRequest(){
        log.info("{}: The current main node is not reachable or set", myRole());
        Set<String> aliveSet = nodeStateService.getActiveNodes().stream().filter(s -> !s.contains(localNodeId)).filter(s -> !s.contains(mainNodeId)).collect(Collectors.toSet());
        if(aliveSet.isEmpty()){
            mainNodeId = localNodeId;
            log.info("{}: I am main bc everyone is dead", myRole());
            storageService.writeSync();
            return true;
        }
        for(String aliveNode : aliveSet){
            log.info("sending election request to node {}", aliveNode);
            Response response = null;
            template.quarkus.common.election.ElectionService es = electionServiceRegistry.getRegistered(aliveNode);
            do {
                aliveSet = nodeStateService.getActiveNodes().stream().filter(s -> !s.contains(localNodeId)).filter(s -> !s.contains(mainNodeId)).collect(Collectors.toSet());
                try {
                    response = es.requestToBecomeMain(new Request(localNodeId, storageService.getLatestVersion()));
                } catch (Exception e) {
                    log.error("{}: Node {} antwortet nicht auf election call. retrying", myRole(),aliveNode);
                    response = null;
                }
                if(!aliveSet.contains(aliveNode))break;
            } while (response == null);
            if(response == null){
                log.error("mein electionpartner {} ist down", aliveNode);
                return false;
            }else if(response.NodeId().equals(localNodeId)){
                mainNodeId = localNodeId;
                log.info("{}: mein election sagt ich bin main", myRole());
                storageService.writeSync();
            }else{
                mainNodeId = response.NodeId();
                log.info("{}: Die node {} ist statt mir main", myRole(), mainNodeId);
            }
        }

        endpointRESTService.updateEndpoint(new EndpointService.Request(mainNodeId));

        nodeStateService.setMainIsDead(false);
        return true;
    }

    public Response respondToElection(Request request){
        log.info("{}: got election Request from Node {}", myRole(), request.nodeId());
        if(isMain()){
            log.info("Ich bim main und gebe mich zurück");
            return new Response(localNodeId, false);
        }
        if(nodeStateService.getActiveNodes().contains(mainNodeId) && !nodeStateService.getMainIsDead()) {
            log.info("main existiert noch");
            return new Response(mainNodeId, false);
        }
        if(request.maxVersion() > storageService.getLatestVersion()){
            mainNodeId = request.nodeId();
            log.info("{}: Accepted {} as new main", myRole(), request.nodeId());
            return new Response(localNodeId, true);
        }else if(request.maxVersion() == storageService.getLatestVersion()){
            mainNodeId = woMainWhenEqualVersion(request.nodeId());
            if(isMain()){
                storageService.writeSync();
                log.info("{}: I am new main bc of Rules", myRole());
                return new Response(localNodeId, false);
            }else{
                log.info("{}: Accepted {} as new main bc of Rules", myRole(), mainNodeId);
                return new Response(mainNodeId, true);
            }
        }else{
            mainNodeId = localNodeId;
            log.info("{}: I am new main bc I have the latest version", myRole());
            storageService.writeSync();
            return new Response(localNodeId, false);
        }
    }

    private String woMainWhenEqualVersion(String otherNodeId){
        if(localNodeId.equals(replicas.get(1))){//node 1
            return replicas.get(1);
        }else if(localNodeId.equals(replicas.get(2))){//node 2
            if(otherNodeId.equals(replicas.get(1)))return replicas.get(1);
            else return replicas.get(1);
        }else if(localNodeId.equals(replicas.get(3))){//node3
            return otherNodeId;
        }
        return localNodeId;
    }

    public String getLocalNodeId(){
        return localNodeId;
    }

    public String getMainNodeId(){
        return mainNodeId;
    }

    public void setMain(String nodeId){
        mainNodeId = nodeId;
    }
}
