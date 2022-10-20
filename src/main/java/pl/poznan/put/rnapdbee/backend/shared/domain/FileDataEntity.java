package pl.poznan.put.rnapdbee.backend.shared.domain;

public class FileDataEntity {
    private String fileName;
    private String fileTitle;

    public FileDataEntity(String fileName, String fileTitle) {
        this.fileName = fileName;
        this.fileTitle = fileTitle;
    }
}
