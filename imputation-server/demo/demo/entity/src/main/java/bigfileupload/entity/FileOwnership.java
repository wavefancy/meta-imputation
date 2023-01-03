package bigfileupload.entity;

import java.util.Date;

public class FileOwnership {
	
    private Long ownershipId;

    private String status;

    private String uploadTime;

    private String updateTime;

    private Long userId;

    private Long fileId;

    private String desc;

    public Long getOwnershipId() {
        return ownershipId;
    }

    public void setOwnershipId(Long ownershipId) {
        this.ownershipId = ownershipId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc == null ? null : desc.trim();
    }
}