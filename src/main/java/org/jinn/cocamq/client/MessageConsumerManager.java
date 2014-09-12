package org.jinn.cocamq.client;

import java.util.ArrayList;
import java.util.List;

import org.jinn.cocamq.commons.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MessageConsumerManager {
	
	private final static Logger logger = LoggerFactory
			.getLogger(MessageConsumerManager.class);
	
	private int fetch_length=1024*12;//default 12k
	private ConsumerZookeeper cz;
	
	private List<MessageConsumer> mcList=new ArrayList<MessageConsumer>();
	
	public void start() {
		cz=new ConsumerZookeeper("/root");
		cz.start("comment");
		logger.info("connected to server successed");
		initConsumer();
		initWorkThread();
	}
	
	private void initConsumer(){
		List<String> brokers = cz.getBrokers();
		for (String broker:brokers) {
			ClientConfig cc=new ClientConfig();
			cc.setNodeValue(broker);
			MessageConsumer mc=new MessageConsumer("comment");
			mc.start();
			mcList.add(mc);
		}
	}
	
	void initWorkThread(){
		for (MessageConsumer element : mcList) {
			WorkThread wt=new WorkThread();
			wt.setDaemon(true);
			wt.mc=element;
			wt.start();
		}
	}
	class WorkThread extends Thread{
		MessageConsumer mc;
		public void run(){
			while(true){
				try {
					mc.fetchMessage(mc.cc.getOffset(), fetch_length);
					logger.info("msg fetched successed......");
				} catch (Exception e) {
					
				}
			}
		}
	}
	public static void main(String[] args) {
		MessageConsumerManager mcm=new MessageConsumerManager();
		mcm.start();
		
	}
}
