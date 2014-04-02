package java_ftp_autoupload;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.LinkedBlockingQueue;

import java_ftp_autoupload.ftp.FTPProcessor;
import java_ftp_autoupload.ftp.command.impl.ChangeWorkingDirectory;
import java_ftp_autoupload.ftp.command.impl.Connect;
import java_ftp_autoupload.ftp.command.impl.DeleteDirectory;
import java_ftp_autoupload.ftp.command.impl.DeleteFile;
import java_ftp_autoupload.ftp.command.impl.Login;
import java_ftp_autoupload.ftp.command.impl.MakeDirectory;
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

	private final String host, username, password, remoteDirectory;

	private final File localDirectory;

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

		localDirectory = new File(config.getProperty("local_directory"));
		remoteDirectory = config.getProperty("remote_directory");

		FTPClient client = new FTPClient();

		// setup the FTP command processor for the FTP client

		ftp = new FTPProcessor(client);

		// setup the file watcher

		watcher = new FileWatcher(localDirectory.getAbsolutePath(), true);

		watcher.addListener(new FileWatcherListener() {

			@Override
			public void onFileChange(FileOperation operation, File file) {
				pending.add(new PendingOperation(operation, file));
			}

		});
	}

	public void stop() throws InterruptedException {

		if (running) {
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

					logger.info("Now processing operation: {}", curr.getOperation());
					
					if (curr.getOperation() == null) {
						logger.info("AutoUploader shutting down.");
						running = false;
						break;
					}

					switch (curr.getOperation()) {

					case CREATE: {
						ftp.put(new Connect(host));
						ftp.put(new Login(username, password));

						String relative = getRelativePath(curr.getFile());

						if (curr.getFile().isDirectory()) {
							
							logger.info("Creating directory: {}", relative);
							
							ftp.put(new MakeDirectory(relative));
						} else {
							
							logger.info("Changing to directory {}", relative);
							
							ftp.put(new ChangeWorkingDirectory(relative));
							
							logger.info("Uploading file: {}", curr.getFile().getName());
							
							ftp.put(new UploadFile(curr.getFile()));
						}

						break;
					}
					case DELETE: {
						ftp.put(new Connect(host));
						ftp.put(new Login(username, password));

						String relative = getRelativePath(curr.getFile());

						if (curr.getFile().isDirectory()) {
							// TODO check this is working properly!
							logger.info("TODO delete directory: {}", relative);
							// ftp.put(new DeleteDirectory(relative));
						} else {
							logger.info("Changing to directory {}", relative);
							
							ftp.put(new ChangeWorkingDirectory(relative));
							
							logger.info("Deleting file: {}", curr.getFile().getName());
							
							ftp.put(new DeleteFile(curr.getFile().getName()));
						}
						
						break;
					}
					case MODIFY: {
						
						ftp.put(new Connect(host));
						ftp.put(new Login(username, password));
						
						String relative = getRelativePath(curr.getFile());
						
						if (curr.getFile().isDirectory()) {
							// TODO
							logger.info("NOT IMPLEMENTED");
						} else {
							
							logger.info("Changing to directory {}", relative);
							
							ftp.put(new ChangeWorkingDirectory(relative));
							ftp.put(new UploadFile(curr.getFile()));
							
						}

						break;
					}
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

	private String getRelativePath(File curr) {

		Path pathBase = Paths.get(localDirectory.toURI());
		Path pathAbsolute = null;

		if (curr.isDirectory()) {
			pathAbsolute = Paths.get(curr.toURI());
		} else {

			pathAbsolute = Paths.get(curr.getParentFile().toURI());
		}

		Path pathRelative = pathBase.relativize(pathAbsolute);
		return "/" + pathRelative.toString();
	}

	public static void main(String... args) throws IOException,
			ConfigurationException {
		AutoUploader au = new AutoUploader();
		au.run();
	}

}
