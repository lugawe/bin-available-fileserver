package template.quarkus.common.content;

import java.util.List;

public record UpdateEntry(int untilVersion, int afterVersion, List<FileVersionEntry> files, String nodeId) {

    public UpdateEntry(int untilVersion, List<FileVersionEntry> files, String nodeId) {
        this(untilVersion, untilVersion - 1, files, nodeId);
    }
}
