package java_ftp_autoupload.ftp.command.impl;

import java.io.IOException;
import java.net.SocketException;

import java_ftp_autoupload.ftp.command.Command;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class Login implements Command {
	private String username, password;

	public Login(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public boolean execute(FTPClient client) throws SocketException,
			IOException {

		if (client.stat() == FTPReply.NOT_LOGGED_IN) {
			client.login(username, password);
			return FTPReply.isPositiveCompletion(client.getReplyCode());
		} else {
			// already logged in
			return true;
		}
	}

}