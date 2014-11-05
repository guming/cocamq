package org.jinn.zktools;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;

public class ZkHandler implements Closeable{
	
	private ZkClient zkClient;
	
	public ZkHandler(ZkConfig zkc) {
		// TODO Auto-generated constructor stub
		this.zkClient=new ZkClient(zkc.zkConnect, zkc.getZkSessionTimeoutMs(),
				zkc.getZkConnectionTimeoutMs(), new StringSerializer());
		
	}
	
	public void createParentPath(final String path){
		final String parentDir = path.substring(0, path.lastIndexOf('/'));
		if(!zkClient.exists(parentDir)){
			zkClient.createPersistent(parentDir);
		}
	}
	
	public void createEphemeralPath(final String path,final String data){
		 try {
			 if(!zkClient.exists(path))
				 zkClient.createEphemeral(path, data);
	     }catch (final ZkNoNodeException e) {
            createParentPath(path);
            zkClient.createEphemeral(path, data);
	     }
	}
	
	public void createPersistentPath(final String path,final String data){
		 try {
			 if(!zkClient.exists(path))
				 zkClient.createPersistent(path, data);
	     }catch (final ZkNoNodeException e) {
          createParentPath(path);
          zkClient.createPersistent(path, data);
	     }
	}
	
	public void updatePersistentPath(final String path, final String data)
	            throws Exception {
        try {
        	zkClient.writeData(path, data);
        }
        catch (final ZkNoNodeException e) {
            createParentPath(path);
            zkClient.createPersistent(path, data);
        }
        catch (Exception e) {
        }
	}
	
    public void updateEphemeralPath(final String path, final String data)
            throws Exception {
        try {
        	zkClient.writeData(path, data);
        }
        catch (final ZkNoNodeException e) {
            createParentPath(path);
            zkClient.createEphemeral(path, data);
        }
    }
	

    public void deletePath(final String path) throws Exception {
        try {
        	zkClient.delete(path);
        }
        catch (final ZkNoNodeException e) {
//            logger.info(path + " deleted during connection loss; this is ok");
        }
        catch (final Exception e) {
            throw e;
        }
    }


    public void deletePathRecursive(final String path) throws Exception {
        try {
        	zkClient.deleteRecursive(path);
        }
        catch (final ZkNoNodeException e) {
//            logger.info(path + " deleted during connection loss; this is ok");

        }
        catch (final Exception e) {
            throw e;
        }
    }


    public List<String> getChildren(final String path) {
        return zkClient.getChildren(path);
    }


    public List<String> getChildrenMaybeNull(final String path) {
        try {
            return zkClient.getChildren(path);
        }
        catch (final ZkNoNodeException e) {
            return null;
        }
    }
	
    public boolean createPathExists(final String path){
		return zkClient.exists(path);
    }
    
	public String readData(final String path){
		return zkClient.readData(path);
	}
	
	public String readDataMaybeNull(final String path) {
	    return zkClient.readData(path, true);
	}
	
	public String createEphemeralSequential(final String path,final String data){
		try {
			return zkClient.createEphemeralSequential(path, data);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "";
	}
	
	public ZkClient getZkClient() {
		return this.zkClient;
	}
	
	public void subscribeDataChanges(String path,IZkDataListener zkDataListener){
		zkClient.subscribeDataChanges(path,zkDataListener);
	}
	
	public void subscribeStateChanges(IZkStateListener zkStateListener){
		zkClient.subscribeStateChanges(zkStateListener);
	}
	
	public void subscribeChildChanges(String path,IZkChildListener zkChildListener){
		zkClient.subscribeChildChanges(path,zkChildListener);
	}
	
	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		getZkClient().close();
	}
	
}
