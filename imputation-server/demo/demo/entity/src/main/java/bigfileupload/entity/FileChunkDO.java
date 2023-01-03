package bigfileupload.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

public class FileChunkDO {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long fileId;
	
    private String status;

    private String uploadTime;

    private String updateTime;

    private Long parentId;
    
    @NotNull(message = "文件大小不能为空")
    private Long fileSize;

    @NotNull(message = "文件分块数量不能为空")
    private Long totalChunks;

    @NotNull(message = "文件标识符不能为空")
    private String identifier;

    @NotNull(message = "文件名称不能为空")
    private String fileName;

    private String fileType;

    private String relativePath;

    private String version;

    private Long referenceCnt;

    private String desc;

    @NotNull(message = "文件本地路径不能为空")
    private String localPath;
    
    private List<FileChunkDTO> fileChunkDTOs = new ArrayList<FileChunkDTO>();

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
	
    public List<FileChunkDTO> getFileChunkDTOs() {
		return fileChunkDTOs;
	}

	public void setFileChunkDTOs(List<FileChunkDTO> fileChunkDTOs) {
		fileChunkDTOs = fileChunkDTOs;
	}

	public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Long getTotalChunks() {
        return totalChunks;
    }

    public void setTotalChunks(Long totalChunks) {
        this.totalChunks = totalChunks;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier == null ? null : identifier.trim();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName == null ? null : fileName.trim();
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType == null ? null : fileType.trim();
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath == null ? null : relativePath.trim();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

    public Long getReferenceCnt() {
        return referenceCnt;
    }

    public void setReferenceCnt(Long referenceCnt) {
        this.referenceCnt = referenceCnt;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc == null ? null : desc.trim();
    }
}