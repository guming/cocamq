package org.jinn.cocamq.util;

public class MemTableCompaction {
	MemTable m1=new MemTable();
	MemTable m2=new MemTable();
	
	public MemTable getM1() {
		return m1;
	}
	
	public MemTable getM2() {
		return m2;
	}

	public void setM1(MemTable m1) {
		this.m1 = m1;
	}

	public void setM2(MemTable m2) {
		this.m2 = m2;
	}
	
}
