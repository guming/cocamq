package org.jinn.cocamq.broker;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;

import org.I0Itec.zkclient.IZkStateListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.jinn.cocamq.util.ConcurrentHashSet;
import org.jinn.cocamq.util.PropertiesUtil;
import org.jinn.zktools.RegisterConfig;
import org.jinn.zktools.ZkConfig;
import org.jinn.zktools.ZkHandler;
import org.jinn.zktools.ZkUtil;


public class BrokerZooKeeper implements PropertyChangeListener{
	
	static final Log log = LogFactory.getLog(BrokerZooKeeper.class);
	public final String metaRoot;
    public final String brokerPath;
    public final String brokerTopicPath;
	private ZkConfig zkconfig;
	private ZkHandler zkh;
	private RegisterConfig rConfig;
	private final Set<String> topics = new ConcurrentHashSet<String>();

	public BrokerZooKeeper(final String root) {
		// TODO Auto-generated constructor stub
		zkconfig=ZkUtil.loadZkConfig();
		zkh=new ZkHandler(zkconfig);
		zkh.createPersistentPath(root, "cocamq");
        this.metaRoot = this.normalize(root);
        this.brokerPath = this.metaRoot + "/brokers";
        this.brokerTopicPath = this.metaRoot + "/brokers/topics";
        this.rConfig=new RegisterConfig(Integer.valueOf(PropertiesUtil.getValue("broker.id")),PropertiesUtil.getValue("broker.host"));
        boolean isMaster=Boolean.valueOf(PropertiesUtil.getValue("broker.master"));
        String topics=PropertiesUtil.getValue("broker.host");
        this.rConfig.setMaster(isMaster);
        this.rConfig.setTopics(topics);
	}
	
	public void start(){
		 zkh.getZkClient().subscribeStateChanges(new SessionExpireListener());
	}
	
	public void registerBrokerInZk(String brokerId,boolean isMaster) throws Exception {
		String path=brokerPath+"/"+brokerId;
		zkh.createPersistentPath(brokerPath, "brokerPath");
		if(isMaster){
			path+="/master";
		}else{
			path+="/slave";
		}
		log.info("path:"+path);
		zkh.createEphemeralPath(path, getZKString(PropertiesUtil.getValue("broker.host"),PropertiesUtil.getValue("broker.port")));
	}
	
	public void registerBrokerTopicInZk(String brokerId,String topic,boolean isMaster) throws Exception {
		String path=brokerTopicPath+"/"+topic;
		zkh.createPersistentPath(brokerTopicPath, topic);
		zkh.createPersistentPath(path, path);
		if(isMaster){
			path+="/master/"+brokerId+"-m";
		}else{
			path+="/slave/"+brokerId+"-s";
		}
		this.topics.add(topic);
		log.info("path:"+path);
		zkh.createEphemeralPath(path, getZKString(PropertiesUtil.getValue("broker.host"),PropertiesUtil.getValue("broker.port")));
	}
	
	public void redoRegisterInZk() throws Exception {
        log.info("reAllRegisterInZk,nodeid: " + this.rConfig.getBrokerId());
        boolean isMaster=Boolean.valueOf(PropertiesUtil.getValue("broker.master"));
        registerBrokerInZk("/"+this.rConfig.getBrokerId(),isMaster);
        for (final String topic : BrokerZooKeeper.this.topics) {
        	this.registerBrokerTopicInZk("/"+this.rConfig.getBrokerId(),topic,isMaster);
        }
        log.info("done reAllRegisterInZk");
    }
	public void unRegisterInZk(String topic) throws Exception {
        log.info("unRegisterInZk,nodeid: " + this.rConfig.getBrokerId());
        log.info("unRegisterInZk,nodeid: " + PropertiesUtil.getValue("broker.master"));
        boolean isMaster=Boolean.valueOf(PropertiesUtil.getValue("broker.master"));
        log.info("isMaster:"+isMaster);
        String master="/slave";
        if(isMaster){
        	master="/master";
		}
        String path=brokerTopicPath+"/"+topic+master+"/"+this.rConfig.getBrokerId()+"-m";
        zkh.deletePath(path);
        log.info("done unRegisterInZk:"+path);
    }
	private class SessionExpireListener implements IZkStateListener {

        @Override
        public void handleNewSession() throws Exception {
        	redoRegisterInZk();
        	log.info("handleNewSession and re-registering server");
        }
        @Override
        public void handleStateChanged(final KeeperState state) throws Exception {
            // do nothing, since zkclient will do reconnect for us.
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
		BrokerZooKeeper bzk=new BrokerZooKeeper("/root");
		try {
			bzk.registerBrokerInZk("100001",true);
			bzk.registerBrokerInZk("100001",false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		//do nothing 
	}
}
