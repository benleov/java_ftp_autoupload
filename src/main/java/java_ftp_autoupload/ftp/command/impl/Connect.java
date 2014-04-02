package java_ftp_autoupload.ftp.command.impl;

import java.io.IOException;
import java.net.SocketException;

import java_ftp_autoupload.ftp.command.Command;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connect implements Command {
	
	private static final Logger logger = LoggerFactory.getLogger(Connect.class);

	private String serverAddress;

	public Connect(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	@Override
	public boolean execute(FTPClient ftp) throws SocketException, IOException {

		if (!ftp.isConnected()) {
			ftp.connect(serverAddress);
			return FTPReply.isPositiveCompletion(ftp.getReplyCode());
		} else {
			logger.info("Already connected");
			return true;
		}
	}

}
