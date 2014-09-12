package org.jinn.cocamq.command;

interface Command {
		public byte[] makeCommand();
		public String getCommand();
}