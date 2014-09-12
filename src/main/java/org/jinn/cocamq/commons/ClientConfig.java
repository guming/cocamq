package org.jinn.cocamq.commons;

public class ClientConfig {
	String nodeId="";
	String nodeValue="";
	String host;
	int port=15001;
	int offset=0;
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public String getNodeValue() {
		return nodeValue;
	}
	public void setNodeValue(String nodeValue) {
		this.nodeValue = nodeValue;
		String temp=nodeValue;
		if(temp.startsWith("cocamq://")){
			temp=temp.replace("cocamq://","");
			String[] str=temp.split(":");
			host=str[0];
			port=Integer.valueOf(str[1]);
		}
	}
	public String getHost() {

		return host;
	}
	public int getPort() {
		return port;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	
}
