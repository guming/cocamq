package org.jinn.cocamq.protocol.command;

interface Command {

		public byte[] makeCommand();

		public String getCommand();
}