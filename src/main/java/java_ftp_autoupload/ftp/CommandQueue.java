package java_ftp_autoupload.ftp;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

import java_ftp_autoupload.ftp.command.Command;

/**

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
		// TODO check if this command is valid
		super.put(object);
	}
	
	@Override
	public boolean addAll(Collection<? extends Command> object) {
		// TODO check if this command is valid
		return super.addAll(object);
	}	
	
 
	private void isDuplicate(Command command) {
		// TODO optimisation of queue (e.g duplicate file modifies)
		
	
		
	}
	
}
