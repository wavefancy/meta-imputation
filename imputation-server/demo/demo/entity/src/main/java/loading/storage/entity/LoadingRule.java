package loading.storage.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

public class LoadingRule {
	private String ruleName;     //入库规则名称或编号
	private String src;          //源数据ID
	private String dst;        //目标库表名称
	private String pluginPath;   //解析插件名称
	private String status;       //入库规则状态：“on”或则“off”

	private String createTime;   //规则创建时间
	private String startTime;    //规则最近一次启动时间
	private String endTime;      //规则最近一次停止时间
	private String createUser;   //创建人
	private String authorizer;   //核准人
	private String dec;          //规则用途描述
	private String rejectedReason; //规则审核未通过的原因
	
	public String getDec() {
		return dec;
	}
	public void setDec(String dec) {
		this.dec = dec;
	}
	public String getRejectedReason() {
		return rejectedReason;
	}
	public void setRejectedReason(String rejectedReason) {
		this.rejectedReason = rejectedReason;
	}
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public String getDst() {
		return dst;
	}
	public void setDst(String dst) {
		this.dst = dst;
	}
	public String getPluginPath() {
		return pluginPath;
	}
	public void setPluginPath(String pluginPath) {
		this.pluginPath = pluginPath;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getAuthorizer() {
		return authorizer;
	}
	public void setAuthorizer(String authorizer) {
		this.authorizer = authorizer;
	}
	
}
