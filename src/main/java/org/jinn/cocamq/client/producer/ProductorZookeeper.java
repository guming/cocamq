package org.jinn.cocamq.client.producer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jinn.cocamq.client.ClientConfig;
import org.jinn.cocamq.util.PropertiesUtil;
import org.jinn.zktools.ZkChildListener;
import org.jinn.zktools.ZkConfig;
import org.jinn.zktools.ZkHandler;
import org.jinn.zktools.ZkUtil;


public class ProductorZookeeper {
	static final Log log = LogFactory.getLog(ProductorZookeeper.class);
	public final String metaRoot;
    public final String productorSubjectPath;
    public final String brokersPath;
	private ZkConfig zkconfig;
	private ZkHandler zkh;
	final List<String> brokers = Collections.synchronizedList(new ArrayList<String>());
	
	public ProductorZookeeper(final String root) {
		// TODO Auto-generated constructor stub
		zkconfig=ZkUtil.loadZkConfig();
		zkh=new ZkHandler(zkconfig);
		if(!zkh.createPathExists(root)){
			zkh.createPathExists(root);
		}
		this.metaRoot = this.normalize(root);
		this.brokersPath=this.metaRoot + "/brokers/topics";
        this.productorSubjectPath = this.metaRoot + "/productors/topics";
	}
	public void start(final String topic){
		String path=brokersPath+"/"+topic+"/master";
		zkh.subscribeChildChanges(path, new BrokerConnectionListener(topic));
		try {
			doFetchBrokers(topic);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void registerTopicInZk(String prodId,String topic) throws Exception {
		String path=productorSubjectPath+"/"+prodId+"/"+topic;
		zkh.createPersistentPath(productorSubjectPath, productorSubjectPath);
		zkh.createPersistentPath(productorSubjectPath+prodId, productorSubjectPath+prodId);
		zkh.createEphemeralPath(path, getZKString(PropertiesUtil.getValue("productor.host"),PropertiesUtil.getValue("productor.port")));
	}
	
	public void doFetchBrokers(String topic) throws Exception {
		String path=brokersPath+"/"+topic;
		List<String> masterList=zkh.getChildrenMaybeNull(path+"/master");
		
		log.info("master path:"+path+"/master");
		log.info("masterList size:"+masterList.size());
		if(masterList!=null&&masterList.size()>0){
			if(!masterList.equals(brokers)){
				brokers.clear();
				brokers.addAll(masterList);
			}
		}else{
			masterList=zkh.getChildrenMaybeNull(path+"/slave");
			log.info("slave path:"+path+"/slave");
			log.info("slaveList size:"+masterList.size());
			if(!masterList.equals(brokers)){
				brokers.clear();
				brokers.addAll(masterList);
			}
		}
	}
	public ClientConfig getMasterBroker(String topic)throws Exception {
		ClientConfig cc=new ClientConfig();
		String path=brokersPath+"/"+topic;
		if(null!=brokers&&brokers.size()>0){
			Collections.shuffle(brokers);
			log.info("brokers.size:"+brokers.size());
			String brokerNodeId=brokers.get(0);//random,0 is tempvalue
			log.info("brokerNodeId:"+brokerNodeId);
			String master=zkh.readDataMaybeNull(path+"/master"+"/"+brokerNodeId);
			if(null==master){
				master=zkh.readDataMaybeNull(path+"/slave"+"/"+brokerNodeId);
				zkh.subscribeChildChanges(path+"/slave", new BrokerConnectionListener(topic));
			}else{
				zkh.subscribeChildChanges(path+"/master", new BrokerConnectionListener(topic));
			}
			cc.setNodeId(brokerNodeId);
			cc.setNodeValue(master);
		}else{
			return null;
		}
		return cc;
	}
	
	final class BrokerConnectionListener implements ZkChildListener {
		
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
