package org.jinn.cocamq.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommonExcutor {
	
	private static final ExecutorService exec = Executors.newFixedThreadPool(100);

	public static ExecutorService getExec() {
		return exec;
	}
	public void shutdown(){
		exec.shutdown();
	}
	

}
