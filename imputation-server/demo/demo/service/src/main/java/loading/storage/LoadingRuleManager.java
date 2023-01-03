package loading.storage;


import java.util.List;

import org.apache.curator.framework.CuratorFramework;

import loading.storage.entity.LoadingRule;



public interface LoadingRuleManager {

	/**新增一条入库规则
	 * @param rule 描述新增规则的详细信息
	 * @return 如果zookeeper集群中已经包含了新增规则，则添加失败返回null，否则返回规则本身
	 * @throws Exception 如果zookeeper操作失败，则抛出异常
	 */
	public LoadingRule AddRule(LoadingRule rule) throws Exception;
	
	/**获取全部入库规则的列表
	 * 
	 * @return 入库规则列表
	 * @throws Exception 无法读取规则时抛出异常
	 */
	public List<LoadingRule> GetRuleList(String src, String dst, String status, String pluginpath, String createuser) throws Exception;
	
	/** 根据入库规则序号，获取特定规则的相关信息
	 * @param zkm zookeeper管理器，详见ZKManager类
	 * @param client 与zookeeper集群通信的连接
	 * @param nodeName 入库规则序号
	 * @return 一条入库规则
	 */
	public LoadingRule GetRule(ZKManager zkm, CuratorFramework client, String nodeName);
	
	/**改变一条入库规则的状态
	 * @param ruleName 入库规则序号
	 * @param status 规则当前所处状态
	 * @param rejectedReason 如果规则没有通过核准，则由该参数给出未通过原因
	 * @param dec 规则描述信息
	 * @param authorizer 规则核准人
	 * @throws Exception 如果zookeeper操作失败，则抛出异常
	 */
	public void ChangeStatus(String ruleName, String newStatus, String rejectedReason, String dec, String authorizer) throws Exception;
	
	/**根据规则序号，删除规则
	 * 
	 * @param ruleName 入库规则序号
	 * @throws Exception 如果zookeeper操作失败，则抛出异常
	 */
	public void RemoveRule(String ruleName) throws Exception;
	
	/**根据规则序号，获取规则状态
	 * 
	 * @param ruleName 入库规则序号
	 * @throws Exception 如果zookeeper操作失败，则抛出异常
	 */	
	public String GetStatus(String ruleName) throws Exception;
	
	/**获取全部解析插件名称
	 * @param pluginPath 插件存储路径
	 * @throws Exception 如果获取失败，则抛出异常
	 */	
	public List<String> GetPluginNames(String pluginPath) throws Exception;
}
