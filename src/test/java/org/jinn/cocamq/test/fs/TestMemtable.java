package org.jinn.cocamq.test.fs;

import org.jinn.cocamq.commons.MemTable;
import org.jinn.cocamq.commons.MemTableCompaction;
import org.jinn.cocamq.client.entity.MessageJson;
import org.jinn.cocamq.storage.MsgFileStorage;
import org.junit.Test;

import com.google.common.base.Stopwatch;

public class TestMemtable {

	@Test
	public void testAppend() {
		MemTable mt = new MemTable();
		Stopwatch sw = new Stopwatch();
		sw.start();
		MessageJson msg = new MessageJson();
//		msg.setTopic("comment");
//		msg.setType(1);
//		msg.setMemo("msg test" + UUID.randomUUID().toString());
//		msg.setIp(11);
//		msg.setUid(45);
//		msg.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
		for (int i = 0; i < 200000; i++) {
//			msg.setId(1000 + i);
//			mt.append(msg);
		}
		sw.stop();
		System.out.println("ex time set:" + sw);
		mt.getSnapShot();
	}

	@Test
	public void testAppend2File() {
		final MsgFileStorage mfs = new MsgFileStorage("comment");
		MemTableCompaction mtc = new MemTableCompaction();
		MemTable mt = new MemTable();
		Stopwatch sw = new Stopwatch();
		sw.start();
		MessageJson msg = new MessageJson();
//		msg.setTopic("comment");
//		msg.setType(1);
//		msg.setMemo("msg test" + UUID.randomUUID().toString());
//		msg.setIp(11);
//		msg.setUid(45);
//		msg.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
		for (int i = 0; i < 80000; i++) {
//			msg.setId(1000 + i);
			// try {
			// MessageQueue.getBqueue().put(msg);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
//			mt.append(msg);
		}
		System.out.println("ex time set:" + sw);
		mtc.setM1(mt);
		sw.stop();
		mt.getSnapShot();
		// System.out.println("queue size:"+MessageQueue.getBqueue().size());
		sw.reset();
		sw.start();
		mfs.appendMessageFromMemTable(mtc);
		// try {
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		sw.stop();
		System.out.println("ex time set2file:" + sw);
	}
}
