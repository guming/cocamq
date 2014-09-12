package org.jinn.zktools.election;

import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;

import org.jinn.zktools.ZkConfig;
import org.jinn.zktools.ZkHandler;
import org.jinn.zktools.ZkUtil;


public class SimpleLeaderSelector extends BaseLeaderSelector implements LeaderSelector{

	public SimpleLeaderSelector(final ZkHandler zkh,final String path,final String data) {
		// TODO Auto-generated constructor stub
		super(zkh, path, data);
	}
	@Override
	public void requeue(boolean reCreate){
		try {
			zkh.deletePath(leadernode);
			if(reCreate){
				leadernode=zkh.createEphemeralSequential(path+LEADER_PATH,data);
				System.out.println("requene node:"+leadernode+",data:"+data);
			}
			leader.set(false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * take simple leadership
	 * @param path
	 * @return
	 */
	@Override
	public void takeLeaderShip(){
		List<String> list=getChildren(true);
		setLeader(list);
	}
	
	@Override
	public void registeWatcher(String path) {
		// TODO Auto-generated method stub
		final IZkChildListener zkChildListener=new IZkChildListener(){
			@Override
			public void handleChildChange(String s, List<String> list)
					throws Exception {
				// TODO Auto-generated method stub
				setLeader(list);
			}
		};
		zkh.subscribeChildChanges(path, zkChildListener);
	}
	
	public static void main(String[] args) {
		ZkConfig zkconfig=ZkUtil.loadZkConfig();
		ZkHandler zkh=new ZkHandler(zkconfig);
		String path="/root/job";
		zkh.createPersistentPath("/root/job","leadership");
		LeaderSelector zls=new SimpleLeaderSelector(zkh,path,"10.1.200.80");
		LeaderSelector zls2=new SimpleLeaderSelector(zkh,path,"10.1.200.82");
		zls.takeLeaderShip();
		zls2.takeLeaderShip();
		System.out.println("80 is "+zls.isLeader());
		System.out.println("82 is "+zls2.isLeader());
		if(zls2.isLeader())
			zls2.requeue(true);
		if(zls.isLeader())
			zls.requeue(true);
		try {
			Thread.sleep(5*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("80 is "+zls.isLeader());
		System.out.println("82 is "+zls2.isLeader());
		zls2.requeue(false);
		zls.requeue(false);
		
	}
}
