package java_ftp_autoupload.ftp.command.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((local == null) ? 0 : local.hashCode());
		return result;
	}

	// TODO java.nio.file.Files.isSameFile(Path, Path)
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		DownloadFile other = (DownloadFile) obj;

		if (local == null) {
			if (other.local != null) {
				return false;
			}
		} else {

			try {
				if (!Files.isSameFile(Paths.get(local.toURI()),
						Paths.get(other.local.toURI()))) {
					return false;
				}
			} catch (IOException e) {
				return false;
			}
		}

		return true;
	}
}
