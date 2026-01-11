package template.quarkus.common.content;

import java.util.List;

public record UpdateEntry(int untilVersion, int afterVersion, List<FileVersionEntry> files) {

    public UpdateEntry(int untilVersion, List<FileVersionEntry> files) {
        this(untilVersion, untilVersion - 1, files);
    }
}
