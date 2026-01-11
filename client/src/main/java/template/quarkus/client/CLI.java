package template.quarkus.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import jakarta.inject.Inject;

import io.quarkus.runtime.QuarkusApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import template.quarkus.common.content.FileEntry;

public class CLI implements QuarkusApplication {

    private static final Logger log = LoggerFactory.getLogger(CLI.class);

    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

    @Inject
    private RESTClient restClient;

    public CLI() {}

    private String readLine() throws IOException {
        log.info("{}", "> ");
        return reader.readLine();
    }

    @Override
    public int run(String... args) throws Exception {
        String cmd = null;
        while (!"exit".equals(cmd)) {
            cmd = readLine();
            restClient.fileService().write(new FileEntry(cmd, "content".getBytes(StandardCharsets.UTF_8)));
        }
        return 0;
    }
}
