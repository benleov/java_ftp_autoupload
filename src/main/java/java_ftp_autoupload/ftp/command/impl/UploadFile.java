package java_ftp_autoupload.ftp.command.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;

import java_ftp_autoupload.ftp.command.Command;

import org.apache.commons.net.ftp.FTPClient;

public class UploadFile implements Command {

	private File file;

	public UploadFile(File file) {
		this.file = file;
	}

	@Override
	public boolean execute(FTPClient client) throws SocketException,
			IOException {

		client.setFileType(FTPClient.BINARY_FILE_TYPE,
				FTPClient.BINARY_FILE_TYPE);
		
		FileInputStream fis = new FileInputStream(file);
		try {
			return client.storeFile(file.getName(), fis);
		} finally {
			fis.close();
		}

	}

}
