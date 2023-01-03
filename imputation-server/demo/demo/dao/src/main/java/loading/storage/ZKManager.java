package loading.storage;

import org.apache.curator.framework.CuratorFramework;

public interface ZKManager {
	
	/**获取某zookeeper数据数据节点的值
	 * @param client 与zookeeper集群通信的连接
	 * @param path 某个zookeeper数据数据节点的路径
	 * @return path对应的数据数据节点的值
	 * @throws Exception 无法获取数据数据节点值时抛出异常
	 */
	public String GetNodeValue(CuratorFramework client, String path) throws Exception;

	/**配置某zookeeper数据数据节点的值
	 * @param client 与zookeeper集群通信的连接
	 * @param path 某个zookeeper数据数据节点的路径
	 * @param value 需要配置的数据数据节点值
	 * @throws Exception 无法配置数据节点值时抛出异常
	 */
	public void SetNodeValue(CuratorFramework client, String path, String value) throws Exception;

	/**在zookeeper集群上创建一个永久数据节点，该数据节点的路径是参数path和一个自增序号拼接而成
	 * @param client 与zookeeper集群通信的连接
	 * @param path 用于拼接数据节点路径
	 * @return 永久数据节点的路径
	 * @throws Exception 无法创建数据节点时抛出异常
	 */
	public String CreatePersistentSequentalZnode(CuratorFramework client, String path) throws Exception;
		
	/**在zookeeper集群上创建一个永久数据节点，该数据节点的路径由参数path指定
	 * @param client 与zookeeper集群通信的连接
	 * @param path 给出数据节点路径
	 * @return 永久数据节点的路径
	 * @throws Exception 无法创建数据节点时抛出异常
	 */
	public String CreatePersistentZnode(CuratorFramework client, String path, byte[] value) throws Exception;	
	
	/**建立与zookeeper集群的连接
	 * 
	 * @return 获得与zookeeper集群的连接
	 */
	public CuratorFramework CreateZKConnection();
	
	/**在zookeeper集群上删除一个永久数据节点及其子节点，该数据节点的路径由参数path指定
	 * @param client 与zookeeper集群通信的连接
	 * @param path 数据节点路径
	 * @throws Exception 无法创建数据节点时抛出异常
	 */
	public void RemoveZnode(CuratorFramework client, String path) throws Exception;
}
