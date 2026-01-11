package template.quarkus.common;

public final class Events {

    public static final String ALIVE_NAME = "alive";
    public static final String ALIVE_DOWN = "alive-down";
    public static final String ALIVE_UP = "alive-up";

    public static final String NODE_NAME = "node";
    public static final String NODE_DOWN = "node-down:";
    public static final String NODE_UP = "node-up:";

    public static final String ELECTION_NAME = "election";

    private Events() {}
}
