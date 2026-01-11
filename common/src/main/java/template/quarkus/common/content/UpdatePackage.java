package template.quarkus.common.content;

import java.util.ArrayList;
import java.util.List;

public class UpdatePackage {

    private int untilVersion;
    private int afterVersion;
    private List<FileVersionEntry> files;

    public UpdatePackage(int untilVersion, int afterVersion, List<FileVersionEntry> files) {
        this.untilVersion = untilVersion;
        this.afterVersion = afterVersion;
        this.files = files;
    }

    public UpdatePackage(int untilVersion, List<FileVersionEntry> files){
        this.untilVersion = untilVersion;
        this.afterVersion = untilVersion -1;
        files = new ArrayList<>();
        files.addAll(files);
    }

    public int getUntilVersion() {
        return untilVersion;
    }

    public int getAfterVersion() {
        return afterVersion;
    }

    public void setUntilVersion(int untilVersion) {
        this.untilVersion = untilVersion;
    }

    public void setAfterVersion(int afterVersion) {
        this.afterVersion = afterVersion;
    }

    public List<FileVersionEntry> getFiles() {
        return files;
    }

    public void setFiles(List<FileVersionEntry> files) {
        this.files = files;
    }
}
