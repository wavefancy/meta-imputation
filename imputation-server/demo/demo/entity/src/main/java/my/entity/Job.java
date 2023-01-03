package my.entity;

public class Job {
    private Long jobId;

    private String jobName;

    private String jobType;

    private String referencePanelList;

    private String arrayBuild;

    private Double rsqFilter;

    private String phasing;

    private String population;

    private String mode;

    private String encryption;

    private String inputFilesPath;

    private Long createUserId;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName == null ? null : jobName.trim();
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType == null ? null : jobType.trim();
    }

    public String getReferencePanelList() {
        return referencePanelList;
    }

    public void setReferencePanelList(String referencePanelList) {
        this.referencePanelList = referencePanelList == null ? null : referencePanelList.trim();
    }

    public String getArrayBuild() {
        return arrayBuild;
    }

    public void setArrayBuild(String arrayBuild) {
        this.arrayBuild = arrayBuild == null ? null : arrayBuild.trim();
    }

    public Double getRsqFilter() {
        return rsqFilter;
    }

    public void setRsqFilter(Double rsqFilter) {
        this.rsqFilter = rsqFilter;
    }

    public String getPhasing() {
        return phasing;
    }

    public void setPhasing(String phasing) {
        this.phasing = phasing == null ? null : phasing.trim();
    }

    public String getPopulation() {
        return population;
    }

    public void setPopulation(String population) {
        this.population = population == null ? null : population.trim();
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode == null ? null : mode.trim();
    }

    public String getEncryption() {
        return encryption;
    }

    public void setEncryption(String encryption) {
        this.encryption = encryption == null ? null : encryption.trim();
    }

    public String getInputFilesPath() {
        return inputFilesPath;
    }

    public void setInputFilesPath(String inputFilesPath) {
        this.inputFilesPath = inputFilesPath == null ? null : inputFilesPath.trim();
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }
}