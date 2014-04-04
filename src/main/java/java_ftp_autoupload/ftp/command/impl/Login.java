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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
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
		Login other = (Login) obj;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

}