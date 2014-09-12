package org.jinn.cocamq.commons;

import java.util.zip.CRC32;

public class CheckCRC32 {
	
	public static final int crc32(byte[] array) {
        return crc32(array, 0, array.length);
    }
  public static final int crc32(byte[] array, int offset, int length) {
        CRC32 crc32 = new CRC32();
        crc32.update(array, offset, length);
        return (int) (crc32.getValue() & 0x7FFFFFFF);
    }
}
