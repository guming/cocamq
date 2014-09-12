package org.jinn.zktools;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class RegisterConfig implements Serializable{

	static final long serialVersionUID = -1L;
	private int brokerId = 0;
    private int serverPort = 15001;
    private String hostName;
    private Set<String> topics=new HashSet<String>();
    private boolean isMaster=true;
	public int getBrokerId() {
		return brokerId;
	}
	public void setBrokerId(int brokerId) {
		this.brokerId = brokerId;
	}
	public int getServerPort() {
		return serverPort;
	}
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public RegisterConfig(int brokerId, String hostName) {
		super();
		this.brokerId = brokerId;
		this.hostName = hostName;
	}
	public Set<String> getTopics() {
		return topics;
	}
	public void setTopics(String topics) {
		Set<String> set_topics=new HashSet<String>();
		String[] topics_str=topics.split(",");
		for (int i = 0; i < topics_str.length; i++) {
			set_topics.add(topics_str[i]);
		}
		this.topics = set_topics;
	}
	public boolean isMaster() {
		return isMaster;
	}
	public void setMaster(boolean isMaster) {
		this.isMaster = isMaster;
	}
    
}
