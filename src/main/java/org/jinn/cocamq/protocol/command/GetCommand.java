package org.jinn.cocamq.protocol.command;


public class GetCommand implements Command {
	private static String command="get";
	private long offset;
	private long fetch_length;
	
	public GetCommand(String command, long offset, long fetch_length) {
		super();
		this.command = command;
		this.offset = offset;
		this.fetch_length = fetch_length;
	}


	@Override
	public byte[] makeCommand() {
		// TODO Auto-generated method stub
		return getCommandContent().getBytes();
	}

	@Override
	public String toString() {
		return "Command [command=" + command + ", offset=" + offset
				+ ", fetch_length=" + fetch_length + "]";
	}
	
	public String getCommandContent(){
		return command+"#"+offset+"#"+fetch_length+"\n";
	}

	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return command;
	}
}
