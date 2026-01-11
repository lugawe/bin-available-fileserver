package template.quarkus.common.election;

public interface ElectionService {

    record Request(String nodeId, int maxVersion) {}

    record Response(boolean ok) {}

    Response requestToBecomeMain(Request request);
}
