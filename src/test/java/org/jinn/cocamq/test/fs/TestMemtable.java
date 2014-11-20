package org.jinn.cocamq.test.fs;

import org.jinn.cocamq.protocol.message.Message;
import org.jinn.cocamq.protocol.message.MessageSent;
import org.jinn.cocamq.util.CheckCRC32;
import org.jinn.cocamq.util.MemTable;
import org.junit.Test;

import com.google.common.base.Stopwatch;

public class TestMemtable {

	@Test
	public void testAppend() {
		MemTable mt = new MemTable();
		Stopwatch sw = new Stopwatch();
		sw.start();
		for (int i = 0; i < 200000; i++) {
            Message msg =getMessage(i);
            mt.append(msg);
		}
		sw.stop();
		System.out.println("ex time set:" + sw);
		mt.getSnapShot();
	}

    private Message getMessage(final int i){
        String temp2="{\"action\":\"edit\",\"redis_key_hash\":\"1\",\"db_key_hash\":\"\"," +
                "\"time\":\"1406168332.35081900\",\"source\":\"web\",\"mars_cid\":\"\"," +
                "\"session_id\":\"\",\"info\":{\"cart_id\":\"6185\",\"user_id\":\""+i+"\",\"brand_id\":\"7511\"," +
                "\"num\":2,\"warehouse\":\"as大劫案快解放但就是放得开束ash侃大山" +
                "ash看动画东方航空上帝会富士康解释都很费劲第三方还是开货到付款导师考核发解释都开发还是看到横峰街道很费劲黑道教父黑道教" +
                "ash看动画东方航空上帝会富士康解释都很费劲第三方还是开货到付款导师考核发解释都开发还是看到横峰街道很费劲黑道教父黑道教" +
                "都很费劲第三方还是开货到付款导师考核发解释都开发还是看到横峰街道很费劲黑道教父黑道教" +
                "ash看动画东方航空上帝会富士康解释都很费劲第三方还师考核发解释都开发还是看到横峰街道很费劲黑道教父黑道教" +
                "父花雕鸡开户行静安寺咁大噶就是个法华经爱就是大是大非带结发华东师范\",\"merchandise_id\":\"1001950\",\"channel\":\"te\"," +
                "\"cart_record_id\":\"8765\",\"size_id\":\"2756943\"}}";

//        String temp2="{\"action\":\"edit\",\"redis_key_hash\":\"1\",\"DB_key_hash\":\"\"," +
//                "\"time\":\"1406168332.35081900\",\"source\":\"web\",\"mars_cid\":\"\"," +
//                "\"session_id\":\"\",\"info\":{\"cart_id\":\"6185\",\"user_id\":\""+i+"\",\"brand_id\":\"7511\"," +
//                "\"num\":2,\"warehouse\":\"asd\",\"merchandise_id\":\"1001950\",\"channel\":\"te\"," +
//                "\"cart_record_id\":\"8765\",\"size_id\":\"2756943\"}}";
        byte[] temp2Bytes=temp2.getBytes();
        int checknum= CheckCRC32.crc32(temp2Bytes);
        Message msg = new MessageSent(checknum,temp2,"comment");
        return msg;
    }
}
