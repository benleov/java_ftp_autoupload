package java_ftp_autoupload.ftp;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

import java_ftp_autoupload.ftp.command.Command;
import java_ftp_autoupload.ftp.command.impl.DownloadFile;
import java_ftp_autoupload.ftp.command.impl.MakeDirectory;
import java_ftp_autoupload.ftp.command.impl.UploadFile;

/**
 * 
 * Holds a queue of <code>Command</code>s.
 * 
 * @author Benjamin Leov
 * 
 */
public class CommandQueue extends LinkedBlockingQueue<Command> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3196761430858932314L;

	@Override
	public void put(Command object) throws InterruptedException {

		addCommand(object);
	}

	@Override
	public boolean addAll(Collection<? extends Command> object) {

		for (Command curr : object) {
			addCommand(curr);
		}

		return !object.isEmpty();
	}

	private void addCommand(Command command) {

		if (contains(command)) {

			if (command instanceof UploadFile) {

				// Processing an upload file request, but this request exists
				// again further up the queue. Delay the uploading by removing it, 
				// and adding this one.

				this.remove(command);
				super.add(command);
			} else if (command instanceof DownloadFile) {
				
				// Processing an download file request, but this request exists
				// again further up the queue. Delay the downloading by removing it, 
				// and adding this one.
				
				this.remove(command);
				super.add(command);
			} else if (command instanceof MakeDirectory) {
				
				// Request to make a directory which already exists on the queue.
				// Ignore the request.
			}
		}
		
		// TODO Remove request to delete a directory thats being made in the queue

		

	}

}
