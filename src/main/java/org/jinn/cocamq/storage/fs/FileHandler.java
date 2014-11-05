package org.jinn.cocamq.storage.fs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;


public interface FileHandler {

	    public long append(ByteBuffer buff) throws IOException;

	    public void flush() throws IOException;

	    public void read(final WritableByteChannel socketChanel,long start,long end) throws IOException;
	    
	    public FileChannel getChannel();

    public boolean checkWrite();

    public long getLimitsize();

    public long getSizeInfile();

    public void close() throws IOException;
}
