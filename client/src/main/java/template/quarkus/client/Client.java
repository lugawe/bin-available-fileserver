package template.quarkus.client;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class Client {

    public Client() {}

    public static void main(String[] args) {
        Quarkus.run(CLI.class, args);
    }
}
