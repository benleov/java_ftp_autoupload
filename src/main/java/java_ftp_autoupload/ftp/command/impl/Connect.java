package java_ftp_autoupload.ftp.command.impl;

import java.io.IOException;
import java.net.SocketException;

import java_ftp_autoupload.ftp.command.Command;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class Connect implements Command {

	private String serverAddress;

	public Connect(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	@Override
	public boolean execute(FTPClient ftp) throws SocketException, IOException {
		ftp.connect(serverAddress);
		return FTPReply.isPositiveCompletion(ftp.getReplyCode());
	}

}
