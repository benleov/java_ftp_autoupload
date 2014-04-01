package java_ftp_autoupload.ftp.command.impl;

import java.io.IOException;
import java.net.SocketException;

import java_ftp_autoupload.ftp.command.Command;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class SetActiveMode implements Command {

	@Override
	public boolean execute(FTPClient ftp) throws SocketException, IOException {
		ftp.mode(FTPClient.ACTIVE_LOCAL_DATA_CONNECTION_MODE);
		return FTPReply.isPositiveCompletion(ftp.getReplyCode());
	}

}