package org.jinn.cocamq.broker;

import java.io.Serializable;

/**
 * request msg which used to send to server
 * @author guming
 *
 */
public class RequestMessage implements Serializable{

		private static final long serialVersionUID = 1L;
		private String cmd;
		private byte[] body=null;
		private long offset=0;
		private long fetch_size=0;
		public RequestMessage() {
		}
		public RequestMessage(final String message) {
			if(null==message){
				cmd="none";
			}else{
				int pos = message.indexOf("#");
				if (pos == -1)
				{
					cmd = message;
				}
				else
				{
					cmd = message.substring(0, pos);
					String body_str = message.substring(pos + 1);
					pos = body_str.indexOf("#");
					offset = Integer.valueOf(body_str.substring(0, pos));
					body_str = body_str.substring(pos + 1);
					if(cmd.equals("set")){
						body= body_str.getBytes();
					}
					if(cmd.equals("get")){
						fetch_size = Integer.valueOf(body_str);
					}
				}
			}
		}
//		public RequestMessage(String cmd, int size) {
//			super();
//			this.cmd = cmd;
//			this.offset = size;
//		}
		
		public String getCmd() {
			return cmd;
		}
		public void setCmd(String cmd) {
			this.cmd = cmd;
		}
		public String getCmdAndBody(){
			if(cmd.equals("get")){
				return cmd+"#"+offset+"#"+fetch_size+"\n";
			}else{
				String body_value=body.toString();

				return cmd+"#"+body_value.length()+"#"+body.toString()+"\n";
			}
		}
//
//    public byte[] getCmdAndBodyByte(){
//        if(cmd.equals("get")){
//            return (cmd+"#"+offset+"#"+fetch_size+"\n").getBytes();
//        }else{
//            int bodylength=body.length;
//            byte[] prefixBytes=(cmd+"#"+bodylength+"#").getBytes();
//
//            byte[] bb=new byte[prefixBytes.length+bodylength+ Constants.END.length];
//
//            return cmd+"#"+body_value.length()+"#"+body+"\n";
//        }
//    }

		public byte[] getBody() {
			return body;
		}
		public void setBody(byte[] body) {
			this.body = body;
		}
		
//		public void setBodyField(String key, Object val){
//			body.put(key, val);
//		}
//
//		public Object getBodyField(String key){
//			return body.get(key);
//		}
		public long getOffset() {
			return offset;
		}
		public void setOffset(long offset) {
			this.offset = offset;
		}
		public long getFetch_size() {
			return fetch_size;
		}
		public void setFetch_size(long fetch_size) {
			this.fetch_size = fetch_size;
		}
		
		
		
}
