package java_ftp_autoupload.ftp.command.impl;

import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;

import java_ftp_autoupload.ftp.command.Command;

public class MakeDirectory implements Command {

	private String pathname;

	public MakeDirectory(String pathname) {
		this.pathname = pathname;
	}

	@Override
	public boolean execute(FTPClient client) throws SocketException,
			IOException {
		return client.makeDirectory(pathname);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((pathname == null) ? 0 : pathname.hashCode());
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
		MakeDirectory other = (MakeDirectory) obj;
		if (pathname == null) {
			if (other.pathname != null)
				return false;
		} else if (!pathname.equals(other.pathname))
			return false;
		return true;
	}

}
