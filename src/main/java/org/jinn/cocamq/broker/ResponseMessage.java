package org.jinn.cocamq.broker;

import java.io.Serializable;

/**
 * reply msg from server
 * @author guming
 *
 */
public class ResponseMessage implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String cmd;
	private int status;
	private int size;
	private String message;
	private long resopnseTime = System.currentTimeMillis();
	public ResponseMessage(){
		
	}
	/**
	 * parse cmd
	 * @param resp
	 */
	public ResponseMessage(String resp)
	{
		int pos = resp.indexOf(" ");
		if (pos == -1)
		{
			cmd = resp;
		}
		else
		{
			cmd = resp.substring(0, pos);
			String jbody = resp.substring(pos + 1);
			pos = jbody.indexOf(" ");
			size = Integer.valueOf(jbody.substring(0, pos));
			jbody = jbody.substring(pos + 1);
		}
	}
	public int getStatus()
	{
		return status;
	}

	public void setStatus(int status)
	{
		this.status = status;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public String toString()
	{
		return cmd + " "+size+" " +"\r\n";//body_value
	}

	public String getCmd()
	{
		return cmd;
	}

	public void setCmd(String cmd)
	{
		this.cmd = cmd;
	}

	public long getResopnseTime() {
		return resopnseTime;
	}

	public void setResopnseTime(long resopnseTime) {
		this.resopnseTime = resopnseTime;
	}

	public static void main(String[] args) {
		ResponseMessage rp1=new ResponseMessage();
		rp1.setCmd("reply");
		rp1.setStatus(1);
		rp1.setMessage("cool");
		System.out.println(rp1.toString());
		ResponseMessage rpm=new ResponseMessage("set#15#{\"message\":\"cool\",\"status\":1}\r\n");
		System.out.println(rpm.message);
	}
}
