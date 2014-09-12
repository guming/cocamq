package org.jinn.cocamq.commons;

import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.jinn.cocamq.entity.Message;


public class MemTable {
	
	private static ConcurrentSkipListMap<Long,Message> mmap=new ConcurrentSkipListMap<Long,Message>();
	
	public static final long limit_size=400*1024*1024l;
	
	public static AtomicLong write_buffer_size=new AtomicLong(0);
	
    public AtomicBoolean mutable=new AtomicBoolean(true);
    
    public AtomicBoolean isFlush=new AtomicBoolean(false);
    
	public static ConcurrentSkipListMap<Long, Message> getMmap() {
		return mmap;
	}
	
	public static long getLimitSize() {
		return limit_size;
	}
	
	public AtomicBoolean getMutable() {
		return mutable;
	}
	
	public void append(Message msg){
		if(mutable.get()==true&&write_buffer_size.addAndGet(msg.getContent().getBytes().length)>limit_size){
			write_buffer_size.addAndGet(-msg.getContent().getBytes().length);
			mutable.set(false);
		}
		if(mutable.get()){
				mmap.put(write_buffer_size.get(), msg);
		}
	}
	public void clear(){
		if(isFlush.get())
			mmap.clear();
	}
	
	public void putFlush(){
		isFlush.set(true);
	}
	public long getSnapShot(){
		System.out.println("mmap size:"+mmap.size());
		return mmap.size();
	}
}
