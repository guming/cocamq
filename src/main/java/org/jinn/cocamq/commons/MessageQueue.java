package org.jinn.cocamq.commons;

import java.util.concurrent.LinkedBlockingQueue;

import org.jinn.cocamq.entity.Message;


public class MessageQueue {
		
		private static LinkedBlockingQueue<Message> bqueue=new LinkedBlockingQueue<Message>();
	
		public static LinkedBlockingQueue<Message> getBqueue() {
			return bqueue;
		}
		
		private static LinkedBlockingQueue<Message> failureQueue=new LinkedBlockingQueue<Message>();

		public static LinkedBlockingQueue<Message> getFailureQueue() {
			return failureQueue;
		}
		
}
