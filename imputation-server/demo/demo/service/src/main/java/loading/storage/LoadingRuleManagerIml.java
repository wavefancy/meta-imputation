package loading.storage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import loading.storage.entity.LoadingRule;
import loading.storage.entity.LoadingStorageResultEnum;
import loading.storage.exception.LoadingStorageException;

@Service
public class LoadingRuleManagerIml implements LoadingRuleManager{
	private String namespace = "/rootnote";    //zookeeper的一个数据节点，所有入库规则都存放在该节点下
	
	@Autowired
	ZKManager zkm;

	private void PreProcessPara(String src, String dst, String pluginpath, String status, String createuser) {
		if(src != null && src.equals(""))
			src = null;
		if(dst != null && dst.equals(""))
			dst = null;
		if(pluginpath != null && pluginpath.equals(""))
			pluginpath = null;
		if(status != null && status.equals(""))
			status = null;
		if(createuser != null && createuser.equals(""))
			createuser = null;
	}
	
	public List<LoadingRule> GetRuleList(String src, String dst, String pluginpath, String status, String createuser) throws Exception {
		CuratorFramework client = zkm.CreateZKConnection(); 
		client.start();
		List<LoadingRule> ruleList = new ArrayList<LoadingRule>();
		List<String> childrenNodeList = null;
		PreProcessPara(src, dst, pluginpath, status, createuser);
			
		try {
			childrenNodeList = client.getChildren().forPath(namespace);
		} catch (Exception e) {
			client.close();
			throw new LoadingStorageException(LoadingStorageResultEnum.GETTINGDATA_FAILED);
		}
		for(String cnp : childrenNodeList) {
			LoadingRule rule = GetRule(zkm, client, cnp);
			if(rule == null)
				continue;
			if((src != null) && (!rule.getSrc().equals(src)))
				continue;
			if((dst != null) && (!rule.getDst().equals(dst)))
				continue;
			if((pluginpath != null) && (!rule.getPluginPath().equals(pluginpath)))
				continue;
			if((status != null) && (!rule.getStatus().equals(status)))
				continue;
			if((createuser != null) && (!rule.getCreateUser().equals(createuser)))
				continue;
			ruleList.add(rule);
		}
		client.close();
		return ruleList;
	}
	
	public LoadingRule GetRule(ZKManager zkm, CuratorFramework client, String nodeName) {
		String path = namespace + "/" + nodeName;
		LoadingRule rule = new LoadingRule();
		rule.setRuleName(nodeName);
		
		try {
			rule.setDst(zkm.GetNodeValue(client, path + "/dst"));
			rule.setSrc(zkm.GetNodeValue(client, path + "/src"));
			rule.setPluginPath(zkm.GetNodeValue(client, path + "/pluginpath"));
			rule.setStatus(zkm.GetNodeValue(client, path + "/status"));
			
			rule.setCreateTime(zkm.GetNodeValue(client, path + "/createtime"));
			rule.setStartTime(zkm.GetNodeValue(client, path + "/starttime"));
			rule.setEndTime(zkm.GetNodeValue(client, path + "/endtime"));
			rule.setCreateUser(zkm.GetNodeValue(client, path + "/createuser"));
			rule.setAuthorizer(zkm.GetNodeValue(client, path + "/authorizer"));
			rule.setDec(zkm.GetNodeValue(client, path + "/dec"));
			rule.setRejectedReason(zkm.GetNodeValue(client, path + "/rejectedreason"));		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return null;
		}
		return rule;
	}
	
	private String ProcessNull(String value) {
		if(value == null)
			return "null";
		else 
			return value;
	}
	
	public LoadingRule AddRule(LoadingRule rule) throws Exception {
		if((rule.getDst() == null) || (rule.getSrc() == null) || (rule.getPluginPath() == null) || (rule.getCreateUser() == null))
			throw new LoadingStorageException(LoadingStorageResultEnum.PARAMISSING);
		List<LoadingRule> ruleList = GetRuleList(null, null, null, null, null);
		String dst = rule.getDst();
		String src = rule.getSrc();
		String PluginPath = rule.getPluginPath();
		
		for(LoadingRule r : ruleList) {
			if(r.getDst().equals(dst) && r.getSrc().equals(src) && r.getPluginPath().equals(PluginPath))
				return null;
		}
		CuratorFramework client = zkm.CreateZKConnection(); 
		client.start();
		try {
			String npath = zkm.CreatePersistentSequentalZnode(client, "/rootnote" + "/rule");
			zkm.CreatePersistentZnode(client, npath + "/dst", ProcessNull(dst).getBytes());
			zkm.CreatePersistentZnode(client, npath + "/src", ProcessNull(src).getBytes());
			zkm.CreatePersistentZnode(client, npath + "/pluginpath", ProcessNull(PluginPath).getBytes());
			zkm.CreatePersistentZnode(client, npath + "/status", ProcessNull(rule.getStatus()).getBytes());
			
			zkm.CreatePersistentZnode(client, npath + "/createtime", ProcessNull(rule.getCreateTime()).getBytes());
			zkm.CreatePersistentZnode(client, npath + "/starttime", ProcessNull(rule.getStartTime()).getBytes());
			zkm.CreatePersistentZnode(client, npath + "/endtime", ProcessNull(rule.getEndTime()).getBytes());
			zkm.CreatePersistentZnode(client, npath + "/createuser", ProcessNull(rule.getCreateUser()).getBytes());
			zkm.CreatePersistentZnode(client, npath + "/authorizer", ProcessNull(rule.getAuthorizer()).getBytes());
			zkm.CreatePersistentZnode(client, npath + "/dec", ProcessNull(rule.getDec()).getBytes());
			zkm.CreatePersistentZnode(client, npath + "/rejectedreason", ProcessNull(rule.getRejectedReason()).getBytes());
		} catch (Exception e) {
			throw new LoadingStorageException(LoadingStorageResultEnum.INPUT_FAILED);
		} finally {
			client.close();
		}
		return rule;
	}
	
	public String GetStatus(String ruleName) throws Exception {
		CuratorFramework client = zkm.CreateZKConnection(); 
		client.start();
		
		String path = namespace + "/" + ruleName + "/status";
		String status = null;
		try {
			status = zkm.GetNodeValue(client, path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new LoadingStorageException(LoadingStorageResultEnum.GETTINGDATA_FAILED);
		} finally {
			client.close();
		}
		return status;
	}
	
	private void ProcUnreviewed(CuratorFramework client, String path, String ruleName
			, String newStatus, String rejectedReason, String authorizer) throws Exception {
		String pathS = path + "/status";
		String pathR = path + "/rejectedreason";
		String pathA = path + "/authorizer";
		
		if(newStatus.equals("rejected")) {
			zkm.SetNodeValue(client, pathS, newStatus);
			zkm.SetNodeValue(client, pathA, authorizer);
			if(rejectedReason != null)
				zkm.SetNodeValue(client, pathR, rejectedReason);
		}
		else if(newStatus.equals("on")) {
			zkm.SetNodeValue(client, pathS, newStatus);
			zkm.SetNodeValue(client, pathA, authorizer);
		}
		else {
			throw new RuntimeException();
		}
	}
	
	private void ProcRejected(CuratorFramework client, String path, String ruleName
			, String newStatus, String dec) throws Exception {
		String pathS = path + "/status";
		String pathD = path + "/dec";
		
		if(newStatus.equals("unreviewed")) {
			zkm.SetNodeValue(client, pathS, newStatus);
			if(dec != null)
				zkm.SetNodeValue(client, pathD, dec);
		}
		else {
			throw new RuntimeException();
		}
	}

	private void ProcOff(CuratorFramework client, String path, String newStatus) throws Exception {
		String pathS = path + "/status";
		String pathSt = path + "/starttime";
		String pathE = path + "/endtime";
		
		if(newStatus.equals("on")) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			zkm.SetNodeValue(client, pathS, newStatus);
			zkm.SetNodeValue(client, pathSt, df.format(new Date()));
			zkm.SetNodeValue(client, pathE, "null");
		}
		else {
			throw new RuntimeException();
		}
	}
	
	private void ProcOn(CuratorFramework client, String path, String newStatus) throws Exception {
		String pathS = path + "/status";
		String pathE = path + "/endtime";
		
		if(newStatus.equals("off")) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	
			zkm.SetNodeValue(client, pathS, newStatus);
			zkm.SetNodeValue(client, pathE, df.format(new Date()));
		}
		else {
			throw new RuntimeException();
		}
	}
	
	public void ChangeStatus(String ruleName, String newStatus, String rejectedReason, String dec, String authorizer) throws Exception {
		CuratorFramework client = zkm.CreateZKConnection(); 
		client.start();

		String path = namespace + "/" + ruleName;
		String oldStatus;
		
		try {
			oldStatus = zkm.GetNodeValue(client, path + "/status");
			if(oldStatus.equals("unreviewed"))
				ProcUnreviewed(client, path, ruleName, newStatus, rejectedReason, authorizer);
			else if(oldStatus.equals("on"))
				ProcOn(client, path, newStatus);
			else if(oldStatus.equals("rejected")) 
				ProcRejected(client, path, ruleName, newStatus, dec);
			else if(oldStatus.equals("off")) 
				ProcOff(client, path, newStatus);
			else
				throw new Exception();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new LoadingStorageException(LoadingStorageResultEnum.CHANGESTATUS_FAILED);
		} finally {
			client.close();
		}
	}
	
	public void RemoveRule(String ruleName) throws Exception {
		if(null == ruleName) {
			throw new LoadingStorageException(LoadingStorageResultEnum.PARAMISSING);
		}
		if(GetStatus(ruleName).equals("on")) {
			throw new LoadingStorageException(LoadingStorageResultEnum.CANNOTREMOVE);
		}
		CuratorFramework client = zkm.CreateZKConnection(); 
		client.start();

		String path = namespace + "/" + ruleName;
		try {
			zkm.RemoveZnode(client, path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new LoadingStorageException(LoadingStorageResultEnum.REMOVE_FAILED);
		} finally {
			client.close();
		}
	}
	
	public List<String> GetPluginNames(String pluginPath) throws Exception {
		List<String> fileList = new ArrayList<String>();
		File fileDir = new File(pluginPath);
		
		if(null == fileDir || !fileDir.isDirectory())
			throw new LoadingStorageException(LoadingStorageResultEnum.PLUGINPATH_ERROR);
		File[] files = fileDir.listFiles();
		if(null == files)
			throw new LoadingStorageException(LoadingStorageResultEnum.NOPLUGINPATH);
		
		for(int i = 0; i < files.length; i++) {   // 如果是文件夹 继续读取
			if(files[i].isDirectory())
				continue;
			String strFileName = files[i].getAbsolutePath();
			if(null == strFileName || !strFileName.endsWith(".jar"))
				continue;
			fileList.add(files[i].getName());
		// 定义的全去文件列表的变量
		}
		if(fileList.isEmpty())
			throw new LoadingStorageException(LoadingStorageResultEnum.NOPLUGINPATH);
	    return fileList;
	}
}
