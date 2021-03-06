package java_ftp_autoupload.ftp.command.impl;

import java.io.IOException;
import java.net.SocketException;

import java_ftp_autoupload.ftp.command.Command;

import org.apache.commons.net.ftp.FTPClient;

public class Disconnect implements Command {
	@Override
	public boolean execute(FTPClient client) throws SocketException,
			IOException {
		client.disconnect();
		return true;
	}

}
