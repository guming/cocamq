package org.jinn.cocamq.storage.exception;

import java.io.File;
import java.io.IOError;

/**
 * Created by cyrus.gu on 2015/1/9.
 */
public class FSError extends IOError
{
    public final File path;

    public FSError(Throwable cause, File path)
    {
        super(cause);
        this.path = path;
    }

    public static FSError findNested(Throwable top)
    {
        for (Throwable t = top; t != null; t = t.getCause())
        {
            if (t instanceof FSError)
                return (FSError) t;
        }

        return null;
    }
}
