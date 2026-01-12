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

    private static final String FILE_NAME = "file.txt";

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
            String[] splitted = cmd.split(" ");
            if ("write".equals(splitted[0])) {
                byte[] content = splitted[1].getBytes(StandardCharsets.UTF_8);
                try {
                    restClient.fileService().write(new FileEntry(FILE_NAME, content));
                } catch (Exception e) {
                    log.error("Connection down", e);
                }
            } else if ("read".equals(splitted[0])) {
                FileEntry fileEntry = null;
                try {
                    fileEntry = restClient.fileService().read(FILE_NAME);
                } catch (Exception e) {
                    log.error("Connection down", e);
                }
                if (fileEntry != null) {
                    byte[] bytes = fileEntry.bytes();
                    if (bytes != null) {
                        log.info("File {}: {}", fileEntry.name(), new String(bytes, StandardCharsets.UTF_8));
                    } else {
                        log.info("File {}: {}", fileEntry.name(), "Leer");
                    }
                }
            }
        }
        return 0;
    }
}
