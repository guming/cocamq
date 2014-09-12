package org.jinn.cocamq.test.netty;

import com.google.common.base.Stopwatch;
import org.apache.log4j.Logger;
import org.jinn.cocamq.client.MessageProductor;
import org.junit.Test;

/**
 * Created by gumingcn on 14-9-11.
 */
public class NettyClosedTest {

    private final static Logger logger = Logger.getLogger(NettyClosedTest.class);
    @Test
    public void testClientTimeOut(){
        MessageProductor mp = new MessageProductor();
        mp.start();
        Stopwatch stopwatch = new Stopwatch();
        stopwatch.start();
        try {
            Thread.sleep(70*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stopwatch.stop();
        logger.info("testClientTimeOut finished:"+stopwatch);
        mp.stop();
    }
}
