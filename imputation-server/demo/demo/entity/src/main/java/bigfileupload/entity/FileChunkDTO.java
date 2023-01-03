package bigfileupload.entity;

import java.util.Date;

public class FileChunkDTO extends FileChunkDTOKey {
    private Long chunkSize;

    private String chunkIdentifier;

    private String uploadTime;

    public Long getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(Long chunkSize) {
        this.chunkSize = chunkSize;
    }

    public String getChunkIdentifier() {
        return chunkIdentifier;
    }

    public void setChunkIdentifier(String chunkIdentifier) {
        this.chunkIdentifier = chunkIdentifier == null ? null : chunkIdentifier.trim();
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }
}