package java_ftp_autoupload.ftp.command.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;

import java_ftp_autoupload.ftp.command.Command;

import org.apache.commons.net.ftp.FTPClient;

public class DownloadFile implements Command {

	private File local;

	public DownloadFile(File local) {
		this.local = local;
	}

	@Override
	public boolean execute(FTPClient client) throws SocketException,
			IOException {

		client.setFileType(FTPClient.BINARY_FILE_TYPE,
				FTPClient.BINARY_FILE_TYPE);
		FileOutputStream fos = new FileOutputStream(local);

		try {
			return client.retrieveFile(local.getName(), fos);
		} finally {
			fos.close();
		}

	}

}
