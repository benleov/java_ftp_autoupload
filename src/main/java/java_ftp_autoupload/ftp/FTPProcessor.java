package java_ftp_autoupload.ftp;

import java.io.IOException;
import java.net.SocketException;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

import java_ftp_autoupload.ftp.command.Command;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Queued command based FTPProcessor, that wraps around the Apache commands FTP
 * library.
 * 
 * @author Benjamin Leov
 * 
 */
public class FTPProcessor implements Runnable {

	private static final Logger logger = LoggerFactory
			.getLogger(FTPProcessor.class);

	private final LinkedBlockingQueue<Command> commands;
	private final FTPClient client;
	private boolean running;

	public FTPProcessor(FTPClient client) {
		this.client = client;
		commands = new LinkedBlockingQueue<>();
	}

	@Override
	public void run() {

		if (!running) {

			running = true;

			while (running) {
				logger.info("Processor starting.");

				try {
					logger.info("Reading command. Commands remaining: {}",
							commands.size());

					Command command = commands.take();

					logger.info("Processing Class: {}", command.getClass().getSimpleName());

					try {
						boolean result = command.execute(client);

						if(!result) {
							logger.warn("Command failed.");
							break;
						}
						
						logger.info("Command finished. Result: {}", result);

					} catch (SocketException e) {
						logger.error("SocketException in processor.", e);
						break;
					} catch (IOException e) {
						logger.error("IOException in processor.", e);
						break;
					}

				} catch (InterruptedException e) {

					// restore the interrupted status
					Thread.currentThread().interrupt();
					running = false;
					continue;
				}

			}
			
			running = false;

		} else {
			logger.warn("Processor already running. Ignoring.");
		}
	}

	/**
	 * Puts another command on the queue to be processed.
	 * 
	 * @param command
	 *            The command to be put on the queue.
	 * 
	 * @throws InterruptedException
	 *             The queue is blocking and thus can throw an
	 *             InterrupedException.
	 */
	public void put(Command command) throws InterruptedException {
		commands.put(command);
	}

	public void addAll(Collection<Command> commands) {
		this.commands.addAll(commands);
	}

}
