package template.quarkus.server.service;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class ElectionService {

    @ConfigProperty(name = "node.main")
    private boolean main;

    public ElectionService() {}

    public boolean isMain() {
        return main;
    }
}
