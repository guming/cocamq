package org.jinn.cocamq.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinn.cocamq.commons.ClientConfig;
import org.jinn.cocamq.util.PropertiesUtil;
import org.jinn.zktools.ZkConfig;
import org.jinn.zktools.ZkHandler;
import org.jinn.zktools.ZkUtil;

public class ConsumerZookeeper {
	
	static final Log log = LogFactory.getLog(ConsumerZookeeper.class);
	public final String metaRoot;
    public final String consumerSubjectPath;
    public final String brokersPath;
	private ZkConfig zkconfig;
	private ZkHandler zkh;
	final List<String> brokers = Collections.synchronizedList(new ArrayList<String>());
	
	public ConsumerZookeeper(final String root) {
		// TODO Auto-generated constructor stub
		zkconfig=ZkUtil.loadZkConfig();
		zkh=new ZkHandler(zkconfig);
		if(!zkh.createPathExists(root)){
			zkh.createPathExists(root);
		}
		this.metaRoot = this.normalize(root);
		this.brokersPath=this.metaRoot + "/brokers/topics";
        this.consumerSubjectPath = this.metaRoot + "/consumers/topics";
	}
	
	public void start(final String topic){
		String path=brokersPath+"/"+topic+"/master";
//		if(log.isDebugEnabled())
		log.info("subscribe child changes path:"+path);
		zkh.subscribeChildChanges(path, new BrokerConnectionListener(topic));
		try {
			registerTopicInZk(PropertiesUtil.getValue("consumer.id"),topic);
			doFetchBrokers(topic);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("ConsumerZookeeper error:"+topic, e);
		}
	}
	
	public void start(){
		final String topic=PropertiesUtil.getValue("consumer.topics");
		start(topic);
	}
	
	public void registerTopicInZk(String prodId,String topic) throws Exception {
		String path=consumerSubjectPath+"/"+prodId+"/"+topic;
		zkh.createPersistentPath(consumerSubjectPath, consumerSubjectPath);
		zkh.createPersistentPath(consumerSubjectPath+prodId, consumerSubjectPath+prodId);
		zkh.createPersistentPath(path, getZKString(PropertiesUtil.getValue("consumer.host"),PropertiesUtil.getValue("consumer.port")));
		if(!zkh.getZkClient().exists(path+"/offset"))
			zkh.createPersistentPath(path+"/offset", "0");
	}
	
	public void doFetchBrokers(String topic) throws Exception {
		String path=brokersPath+"/"+topic;
		List<String> masterList=zkh.getChildrenMaybeNull(path+"/slave");
		
		if(masterList!=null&&masterList.size()>0){
			log.info("slave path:"+path+"/slave");
			log.info("slaveList size:"+masterList.size());
			if(!masterList.equals(brokers)){
				brokers.clear();
				brokers.addAll(masterList);
			}
		}else{
			masterList=zkh.getChildrenMaybeNull(path+"/master");
			log.info("master path:"+path+"/master");
			log.info("masterlist size:"+masterList.size());
			if(!masterList.equals(brokers)){
				brokers.clear();
				brokers.addAll(masterList);
			}
		}
	}
	public int readFetchOffset(String topic){
		String path=consumerSubjectPath+"/"+PropertiesUtil.getValue("consumer.id")+"/"+topic+"/offset";
		String offset=zkh.readDataMaybeNull(path);
		if(null==offset||offset.equals("")){
			return 0;
		}else{
			return Integer.valueOf(offset);
		}
	}
	public void updateFetchOffset(String topic,long offset) throws Exception{
		log.info("fetch offset:"+offset);
		String path=consumerSubjectPath+"/"+PropertiesUtil.getValue("consumer.id")+"/"+topic+"/offset";
		zkh.updatePersistentPath(path,offset+"");
		log.info(zkh.readData(path));
	}
	
	public List<String> getBrokers() {
		return brokers;
	}
	
	public ClientConfig getMasterBroker(String topic)throws Exception {
		ClientConfig cc=new ClientConfig();
		String path=brokersPath+"/"+topic;
		if(null!=brokers&&brokers.size()>0){
			Collections.shuffle(brokers);
			log.info("brokers.size:"+brokers.size());
			String brokerNodeId=brokers.get(0);//random,0 is tempvalue
			log.info("brokerNodeId:"+brokerNodeId);
			String master=zkh.readDataMaybeNull(path+"/slave"+"/"+brokerNodeId);
			if(null==master){
				master=zkh.readDataMaybeNull(path+"/master"+"/"+brokerNodeId);
			}
			cc.setNodeId(brokerNodeId);
			cc.setNodeValue(master);
		}else{
			return null;
		}
		return cc;
	}
	
	final class BrokerConnectionListener implements IZkChildListener{
		
		final String topic;
		public BrokerConnectionListener(String topic) {
			super();
			this.topic = topic;
		}
		@Override
		public void handleChildChange(String arg0, List<String> arg1)
				throws Exception {
			// TODO Auto-generated method stub
			log.info("productor handleChildChange:"+topic);
			doFetchBrokers(topic);
		}
		
	}
	
	public String getZKString(String host,String port) {
		
	        if (host.contains(":")) {
	            if (host.startsWith("[")) {
	                return "cocamq://" + host + ":" + port;
	            }
	            else {
	                return "cocamq://[" + host + "]:" + port;
	            }
	        }
	        else {
	            return "cocamq://" + host + ":" + port;
	        }
	}
	
	public static void main(String[] args) {
		
	}
	
	private String normalize(final String root) {
        if (root.startsWith("/")) {
            return this.removeLastSlash(root);
        }
        else {
            return "/" + this.removeLastSlash(root);
        }
	}
	
	private String removeLastSlash(final String root) {
        if (root.endsWith("/")) {
            return root.substring(0, root.lastIndexOf("/"));
        }
        else {
            return root;
        }
    }
}
