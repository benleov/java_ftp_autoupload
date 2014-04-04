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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((serverAddress == null) ? 0 : serverAddress.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Connect other = (Connect) obj;
		if (serverAddress == null) {
			if (other.serverAddress != null)
				return false;
		} else if (!serverAddress.equals(other.serverAddress))
			return false;
		return true;
	}

}
