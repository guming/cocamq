package org.jinn.cocamq.test.netty;

import org.jinn.cocamq.entity.RequestMessage;
import org.junit.Test;

public class RequestMessageTest {
	@Test
	public void testGetCommand(){
		RequestMessage rm=new RequestMessage("get#0#1024");
		System.out.println(rm.getCmdAndBody());
		System.out.println(rm.getCmd());
		System.out.println(rm.getFetch_size());
		System.out.println(rm.getOffset());
	}
	@Test
	public void testSetCommand(){
		RequestMessage rm=new RequestMessage("set#2#{}");
		System.out.println(rm.getCmdAndBody());
		System.out.println(rm.getCmd());
		System.out.println(rm.getOffset());
	}
}
