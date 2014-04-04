package java_ftp_autoupload.ftp;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

import java_ftp_autoupload.ftp.command.Command;
import java_ftp_autoupload.ftp.command.impl.UploadFile;

/**
 * 
 * @author Benjamin Leov
 * 
 * @param <E>
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

		if (command instanceof UploadFile && contains(command)) {

			// Processing an upload file request, but this request exists again
			// further up the queue. Delay the uploading by removing it, and
			// adding this one.

			this.remove(command);
		}

		
		super.add(command);

	}

}
