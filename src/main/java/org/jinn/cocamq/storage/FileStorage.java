package org.jinn.cocamq.storage;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.jinn.cocamq.util.MemTableCompaction;
import org.jinn.cocamq.protocol.message.Message;
import org.jinn.cocamq.storage.fs.PageStorage;
/**
 * MsgFileStorage interface for call
 * @author guming
 *
 */
public class FileStorage implements MessageStorage{
	
	private final ConcurrentHashMap<String,PageStorage> fsMap=new ConcurrentHashMap<String,PageStorage>();
	
	public FileStorage(String topics) {
		super();
		String[] topic_arr=topics.split(",");
		for (int i = 0; i < topic_arr.length; i++) {
			String topic=topic_arr[i];
			PageStorage ps=new PageStorage(topic);
			fsMap.put(topic, ps);
		}
	}
	public PageStorage getFSWithTopic(String topic){
		return fsMap.get(topic);
	}
	@Override
	public void appendMessage(Message msg) {
		try {
			getFSWithTopic(msg.getTopic()).append(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void appendMessageFromMemTable(MemTableCompaction mtc) {
		try {
			for (Iterator<Message> iterator = mtc.getM1().getMmap().values().iterator(); iterator.hasNext();) {
				
				Message msg = (Message) iterator.next();
				getFSWithTopic(msg.getTopic()).append(msg);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * number is the last update offset by consumer
	 */
	@Override
	public void fetchTopicsMessages(final WritableByteChannel socketChanel,final long offset,final long range, String topics) {
		getFSWithTopic(topics).read(socketChanel, offset,range);
	}
	
	class StorageWatcher implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
	}

}
