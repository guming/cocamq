package org.jinn.cocamq.commons;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommonExcutor {
	
	private static final ExecutorService exec = Executors.newFixedThreadPool(100);
	private static final ExecutorService exec2 = Executors.newFixedThreadPool(100);
	private static final ExecutorService exec3 = Executors.newFixedThreadPool(100);
	public static ExecutorService getExec() {
		return exec;
	}
	public void shutdown(){
		exec.shutdown();
	}
	
	public static ExecutorService getExec2() {
		return exec2;
	}
	public void shutdown2(){
		exec2.shutdown();
	}
	
	public static ExecutorService getExec3() {
		return exec3;
	}
	public void shutdown3(){
		exec3.shutdown();
	}
}
