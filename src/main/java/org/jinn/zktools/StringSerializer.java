package org.jinn.zktools;

import java.io.UnsupportedEncodingException;

import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

public class StringSerializer implements ZkSerializer{

	@Override
	public Object deserialize(byte[] abyte0) throws ZkMarshallingError {
		// TODO Auto-generated method stub
		try {
			return new String(abyte0, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public byte[] serialize(Object obj) throws ZkMarshallingError {
		// TODO Auto-generated method stub
		 try {
			return ((String) obj).getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return null;
	}
	
}
