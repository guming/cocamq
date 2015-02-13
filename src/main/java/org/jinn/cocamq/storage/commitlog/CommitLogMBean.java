package org.jinn.cocamq.storage.commitlog;

/**
 * Created by cyrus.gu on 2015/1/9.
 */
public interface CommitLogMBean {

    public long getCompletedTasks();

    public long getPendingTasks();


}
