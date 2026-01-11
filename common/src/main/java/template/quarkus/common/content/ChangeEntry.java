package template.quarkus.common.content;

import java.util.List;

public record ChangeEntry(int version, List<FileEntry> files) {}
