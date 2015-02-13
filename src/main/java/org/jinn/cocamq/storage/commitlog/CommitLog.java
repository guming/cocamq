package org.jinn.cocamq.storage.commitlog;

/**
 * Created by cyrus.gu on 2015/1/9.
 */
public class CommitLog implements CommitLogMBean {
    @Override
    public long getCompletedTasks() {
        return 0;
    }

    @Override
    public long getPendingTasks() {
        return 0;
    }

}
