package org.jinn.zktools.election;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jinn.zktools.ZkHandler;

public abstract class BaseLeaderSelector{
	final ZkHandler zkh;
	String path;
	String data;
	String leadernode;
	AtomicBoolean leader=new AtomicBoolean(false);
	static final String LEADER_PATH="/leader-";
	public BaseLeaderSelector(final ZkHandler zkh,final String path,final String data) {
		// TODO Auto-generated constructor stub
		this.zkh=zkh;
		this.path=path;
		this.data=data;
		leadernode=zkh.createEphemeralSequential(path+LEADER_PATH,data);
		System.out.println("init node:"+leadernode+",data:"+data);
	}

	public boolean isLeader() {
		// TODO Auto-generated method stub
		return leader.get();
	}
	
	public List<String> getChildren(boolean toWatcher){
		List<String> list=zkh.getChildrenMaybeNull(path);
		if(toWatcher)
			registeWatcher(path);
		return list;
	}
	public abstract void registeWatcher(String path);
	
	public void setLeader(List<String> list) {
		sortChild(list);
		System.out.println("childnode size:"+list.toString());
		if(list==null||list.size()==0){
			leader.set(false);
		}else{
			String node2=zkh.readData(path+"/"+list.get(0));
			if(node2.endsWith(data)){
				leader.set(true);
			}
			else{
				leader.set(false);
			}
		}
	}
	
	private void sortChild(List<String> list){
		Collections.sort(list, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				// TODO Auto-generated method stub
				int start1=Integer.valueOf(o1.substring(7));
				int start2=Integer.valueOf(o2.substring(7));
				if (start1 == start2) {
					return 0;
				} else if (start1 > start1) {
					return 1;
				} else {
					return -1;
				}
			}
		});
		Collections.sort(list);
	}
}
