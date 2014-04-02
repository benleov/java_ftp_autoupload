package java_ftp_autoupload.ftp.command;

import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;

public interface Command {

	/**
	 * represents an FTP command.
	 * 
	 * @return true if successful
	 */
	boolean execute(FTPClient client) throws SocketException, IOException;
}