package java_ftp_autoupload.ftp.command.impl;

import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;

import java_ftp_autoupload.ftp.command.Command;

public class DeleteDirectory implements Command {

	private String pathname;

	public DeleteDirectory(String pathname) {
		this.pathname = pathname;
	}

	@Override
	public boolean execute(FTPClient client) throws SocketException,
			IOException {
		return client.removeDirectory(pathname);
	}

}
