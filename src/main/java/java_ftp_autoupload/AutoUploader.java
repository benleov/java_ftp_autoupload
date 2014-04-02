package java_ftp_autoupload;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import java_ftp_autoupload.ftp.FTPProcessor;
import java_ftp_autoupload.ftp.command.impl.ChangeWorkingDirectory;
import java_ftp_autoupload.ftp.command.impl.Connect;
import java_ftp_autoupload.ftp.command.impl.DeleteFile;
import java_ftp_autoupload.ftp.command.impl.Login;
import java_ftp_autoupload.ftp.command.impl.UploadFile;
import java_ftp_autoupload.watcher.FileOperation;
import java_ftp_autoupload.watcher.FileWatcher;
import java_ftp_autoupload.watcher.FileWatcherListener;
import lib.config.base.configuration.ConfigurationException;
import lib.config.base.configuration.ConfigurationList;
import lib.config.base.configuration.factory.ConfigurationFactory;
import lib.config.base.configuration.impl.BasicConfiguration;
import lib.config.base.configuration.persist.impl.IniPersister;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoUploader implements Runnable {

	private static final Logger logger = LoggerFactory
			.getLogger(AutoUploader.class);

	private static final String SETTINGS_FILE = "auto_uploader.ini";

	private boolean running;

	private final FTPProcessor ftp;
	private final FileWatcher watcher;

	private final String host, username, password, localDirectory,
			remoteDirectory;

	private final LinkedBlockingQueue<PendingOperation> pending;

	public AutoUploader() throws IOException, ConfigurationException {

		pending = new LinkedBlockingQueue<>();

		// read in settings from the filesystem

		File settingsFile = new File(SETTINGS_FILE);

		IniPersister<BasicConfiguration> persister = new IniPersister<BasicConfiguration>(
				new ConfigurationFactory<BasicConfiguration>() {

					@Override
					public BasicConfiguration buildConfiguration(String id) {
						return new BasicConfiguration(id);
					}
				}, settingsFile);

		ConfigurationList<BasicConfiguration> settings = persister.read();
		BasicConfiguration config = settings.getConfigurations().get(0);

		host = config.getProperty("host");
		username = config.getProperty("username");
		password = config.getProperty("password");

		localDirectory = config.getProperty("local_directory");
		remoteDirectory = config.getProperty("remote_directory");

		FTPClient client = new FTPClient();

		// setup the FTP command processor for the FTP client

		ftp = new FTPProcessor(client);

		// setup the file watcher

		watcher = new FileWatcher(localDirectory, true);

		watcher.addListener(new FileWatcherListener() {

			@Override
			public void onFileChange(FileOperation operation, File file) {
				pending.add(new PendingOperation(operation, file));
			}

		});
	}

	public void stop() throws InterruptedException {

		if (running) {
			running = false;

			// poison pill shutdown
			pending.put(new PendingOperation(null, null));
		}
	}

	class PendingOperation {
		private File file;
		private FileOperation operation;

		public PendingOperation(FileOperation operation, File file) {
			this.operation = operation;
			this.file = file;
		}

		public File getFile() {
			return file;
		}

		public FileOperation getOperation() {
			return operation;
		}

	}

	@Override
	public void run() {

		new Thread(watcher).start();
		new Thread(ftp).start();

		if (!running) {

			running = true;

			while (running) {

				// process the pending file operations queue

				try {
					PendingOperation curr = pending.take();

					if (curr.getOperation() == null) {
						// shutdown
						logger.info("AutoUploader shutting down.");
						continue;
					}

					switch (curr.getOperation()) {

					case CREATE:
						ftp.put(new Connect(host));
						ftp.put(new Login(username, password));
						ftp.put(new ChangeWorkingDirectory(remoteDirectory));
						ftp.put(new UploadFile(curr.getFile()));
						break;
					case DELETE:
						ftp.put(new Connect(host));
						ftp.put(new Login(username, password));
						ftp.put(new ChangeWorkingDirectory(remoteDirectory));
						ftp.put(new DeleteFile(curr.getFile().getName()));
						break;
					case MODIFY:
						ftp.put(new Connect(host));
						ftp.put(new Login(username, password));
						ftp.put(new ChangeWorkingDirectory(remoteDirectory));
						ftp.put(new UploadFile(curr.getFile()));
						break;
					case UNKNOWN:
						logger.error("Unknown operation for file: {}",
								curr.getFile());
						break;
					default:
						logger.error("Unknown operation: {} for file: {}",
								curr.getOperation(), curr.getFile());
						break;
					}

				} catch (InterruptedException e) {
					// restore the interrupted status
					Thread.currentThread().interrupt();
					running = false;
					continue;
				}
			}

		} else {
			logger.warn("AutoUploader has already started.");
		}

	}

	public static void main(String... args) throws IOException,
			ConfigurationException {
		AutoUploader au = new AutoUploader();
		au.run();
	}

}
