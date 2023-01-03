package bigfileupload.entity;

public class FileChunkDTOKey {
    private Integer chunkNumber;

    private String fileIdentifier;

    private Long userid;

    public Integer getChunkNumber() {
        return chunkNumber;
    }

    public void setChunkNumber(Integer chunkNumber) {
        this.chunkNumber = chunkNumber;
    }

    public String getFileIdentifier() {
        return fileIdentifier;
    }

    public void setFileIdentifier(String fileIdentifier) {
        this.fileIdentifier = fileIdentifier == null ? null : fileIdentifier.trim();
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }
}