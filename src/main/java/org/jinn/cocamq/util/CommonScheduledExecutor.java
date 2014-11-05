package org.jinn.cocamq.util;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CommonScheduledExecutor {
	private static final ScheduledExecutorService scheduler =
		       Executors.newScheduledThreadPool(1);

    public static void excute(Runnable run) {
            scheduler.scheduleAtFixedRate(run, 10, 10, SECONDS);
    }
    public static void excute(Runnable run,int delay,TimeUnit timeUnit) {
        scheduler.schedule(run, delay, timeUnit);
}
}
