package loading.storage;

import java.util.List;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public class ZKManagerIml implements ZKManager{
	//zookeeper服务器集群的<IP:端口>列表
	private static String connectionInfo = "192.168.0.1:2181";//, 192.168.0.2:2181, 192.168.0.3:2181, 192.168.0.4:2181
	
	public String GetNodeValue(CuratorFramework client, String path) throws Exception {
		Stat stat = new Stat();
		//获取节点值，并同时获取数据节点状态信息
		byte[] data = client.getData().storingStatIn(stat).forPath(path);
		//更新数据节点
		String sdata = new String(data);
		return sdata;
//		System.out.println(path + "  数据:" + sdata);
	}
	
	public void SetNodeValue(CuratorFramework client, String path, String value) throws Exception{
		Stat stat = new Stat();
		//获取节点值，并同时获取节点状态信息
		byte[] data = client.getData().storingStatIn(stat).forPath(path);
		//更新节点
		int ver = stat.getVersion();
		client.setData().withVersion(ver)  //版本校验，与当前版本不一致则更新失败,默认值-1无视版本信息进行更新
		      .forPath(path, value.getBytes());
		data = client.getData().forPath(path);
	//	System.out.println(path + "  数据:" + new String(data));
	}
	
	public String CreatePersistentSequentalZnode(CuratorFramework client, String path) throws Exception{
		String npath = client.create().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(path);
	//	System.out.println(path + "  数据:" + new String(npath));
		return npath;
	}

	public String CreatePersistentZnode(CuratorFramework client, String path, byte[] value) throws Exception{
		String npath = client.create().forPath(path, value);
	//	System.out.println(path + "  数据:" + new String(npath));
		return npath;
	}
	
	public CuratorFramework CreateZKConnection() {
		CuratorFramework client = null;
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(100, 2);
		client = CuratorFrameworkFactory.builder()
					.connectString(connectionInfo)
					.sessionTimeoutMs(100000)
					.connectionTimeoutMs(100000)
					.retryPolicy(retryPolicy)
					.namespace("")
					.build();
		return client;
	}
	
	public void RemoveZnode(CuratorFramework client, String path) throws Exception{
		List<String> childrenNodeList = client.getChildren().forPath(path);

		for(String cnp : childrenNodeList) {
			client.delete().forPath(path + "/" + cnp);
		}
		client.delete().forPath(path);
//		System.out.println(path + " 删除");
	}
}
