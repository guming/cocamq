package org.jinn.cocamq.storage.commitlog;

import org.jinn.cocamq.storage.exception.FSError;
import org.jinn.cocamq.storage.exception.MessageException;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

/**
 * Created by cyrus.gu on 2015/1/9.
 */
public class CommitLogDescriptor {

    private static final String SEPARATOR = "-";
    private static final String FILENAME_PREFIX = "CommitLog" + SEPARATOR;
    private static final String FILENAME_EXTENSION = ".log";
    private static final Pattern COMMIT_LOG_FILE_PATTERN = Pattern.compile(FILENAME_PREFIX
            + "((\\d+)(" + SEPARATOR + "\\d+)?)" + FILENAME_EXTENSION);
    public static final int VERSION_1 = 1;
    public static final int current_version = VERSION_1;
    // [version, id, checksum]
    static final int HEADER_SIZE = 4 + 8 + 8;
    final int version;
    public final long id;
    public CommitLogDescriptor(int version, long id)
    {
        this.version = version;
        this.id = id;
    }
    public CommitLogDescriptor(long id)
    {
        this(current_version, id);
    }
    static void writeHeader(ByteBuffer out, CommitLogDescriptor descriptor)
    {
        out.putInt(0, descriptor.version);
        out.putLong(4, descriptor.id);
        CRC32 crc = new CRC32();
        crc.update(descriptor.version);
        crc.update((int) (descriptor.id & 0xFFFFFFFFL));
        crc.update((int) (descriptor.id >>> 32));
        out.putLong(12, crc.getValue());
    }

    public static CommitLogDescriptor fromHeader(File file)
    {
        try (RandomAccessFile raf = new RandomAccessFile(file, "r"))
        {
            assert raf.getFilePointer() == 0;
            int version = raf.readInt();
            long id = raf.readLong();
            int crc = raf.readInt();
            CRC32 checkcrc = new CRC32();
            checkcrc.update(version);
            checkcrc.update((int) (id & 0xFFFFFFFFL));
            checkcrc.update((int) (id >>> 32));
            if (crc == checkcrc.getValue())
                return new CommitLogDescriptor(version, id);
            return null;
        }
        catch (EOFException e)
        {
            throw new RuntimeException(e);
        }
        catch (IOException e)
        {
            throw new FSError(e,file);
        }
    }

    public static CommitLogDescriptor fromFileName(String name)
    {
        Matcher matcher;
        if (!(matcher = COMMIT_LOG_FILE_PATTERN.matcher(name)).matches())
            throw new RuntimeException("Cannot parse the version of the file: " + name);

        if (matcher.group(3) == null)
            throw new UnsupportedOperationException("Commitlog segment is too old to open; upgrade to 1.2.5+ first");

        long id = Long.parseLong(matcher.group(3).split(SEPARATOR)[1]);
        return new CommitLogDescriptor(Integer.parseInt(matcher.group(2)), id);
    }

    public String fileName()
    {
        return FILENAME_PREFIX + version + SEPARATOR + id + FILENAME_EXTENSION;
    }

    public static boolean isValid(String filename)
    {
        return COMMIT_LOG_FILE_PATTERN.matcher(filename).matches();
    }

    @Override
    public String toString() {
        return "CommitLogDescriptor{" +
                "version=" + version +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommitLogDescriptor that = (CommitLogDescriptor) o;

        if (id != that.id) return false;
        if (version != that.version) return false;

        return true;
    }

}
