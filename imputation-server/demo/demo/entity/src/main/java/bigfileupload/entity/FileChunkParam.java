package bigfileupload.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

public class FileChunkParam {
	@NotNull(message = "文件ID不能为空")
	private Long fileId;

	@NotNull(message = "当前分片不能为空")
    private Integer chunkNumber;

    @NotNull(message = "分片大小不能为空")
    private Long chunkSize;

    @NotNull(message = "分片总数不能为空")
    private Integer totalChunks;

    @NotBlank(message = "文件标识不能为空")
    private String identifier;//文件md5

    @NotNull(message = "文件总大小不能为空")
    private Long totalSize;
    
    @NotBlank(message = "数据块标识不能为空")
    private String chunk_identifier;//数据块md5

	private String fileName;
    
	private MultipartFile file;
    
    public String getChunk_identifier() {
		return chunk_identifier;
	}

	public void setChunk_identifier(String chunk_identifier) {
		this.chunk_identifier = chunk_identifier;
	}

    public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
    
    public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	public Integer getChunkNumber() {
		return chunkNumber;
	}

	public void setChunkNumber(Integer chunkNumber) {
		this.chunkNumber = chunkNumber;
	}

	public Long getChunkSize() {
		return chunkSize;
	}

	public void setChunkSize(Long chunkSize) {
		this.chunkSize = chunkSize;
	}

	public Integer getTotalChunks() {
		return totalChunks;
	}

	public void setTotalChunks(Integer totalChunks) {
		this.totalChunks = totalChunks;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public Long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(Long totalSize) {
		this.totalSize = totalSize;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

}