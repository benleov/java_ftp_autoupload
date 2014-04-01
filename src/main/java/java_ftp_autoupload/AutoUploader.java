package java_ftp_autoupload;

import java.io.File;
import java.io.IOException;

import java_ftp_autoupload.ftp.FTPProcessor;
import java_ftp_autoupload.ftp.command.impl.Connect;
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

public class AutoUploader {

	private static final String SETTINGS_FILE = "auto_uploader.ini";

	private boolean running;

	public AutoUploader(String localDirectory, String remoteDirectory)
			throws IOException, ConfigurationException {

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

		String host = config.getProperty("host");
		String username = config.getProperty("username");
		String password = config.getProperty("password");

		FTPClient client = new FTPClient();

		// setup the FTP command processor for the FTP client

		FTPProcessor processor = new FTPProcessor(client);

		// setup the file watcher

		FileWatcher watcher = new FileWatcher(localDirectory, true);

		watcher.addListener(new FileWatcherListener() {

			@Override
			public void onFileChange(FileOperation operation, File file) {

				switch (operation) {
				case CREATE:
					// TODO
					break;
				case DELETE:
					// TODO
					break;
				case MODIFY:
					// TODO
				case UNKNOWN:
					// TODO
					break;
				default:
					// TODO
					break;
				}

			}

		});

		new Thread(watcher).start();
		new Thread(processor).start();

	}

	private void connect(FTPProcessor processor, String host, String username,
			String password) throws InterruptedException {
		processor.put(new Connect(host));
		processor.put(new Login(username, password));
	}

}
