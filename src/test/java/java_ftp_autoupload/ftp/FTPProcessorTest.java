package java_ftp_autoupload.ftp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import java_ftp_autoupload.ftp.command.Command;
import java_ftp_autoupload.ftp.command.impl.Connect;
import java_ftp_autoupload.ftp.command.impl.Disconnect;
import java_ftp_autoupload.ftp.command.impl.Login;
import java_ftp_autoupload.ftp.command.impl.Logout;
import java_ftp_autoupload.ftp.command.impl.UploadFile;
import lib.config.base.configuration.ConfigurationList;
import lib.config.base.configuration.factory.ConfigurationFactory;
import lib.config.base.configuration.impl.BasicConfiguration;
import lib.config.base.configuration.persist.impl.IniPersister;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FTPProcessorTest {

	private static final String SETTINGS_FILE = "junit_settings.ini";
	
	private String username = null;
	private String password = null;
	private String host = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
		File temp = new File(SETTINGS_FILE);
		
		IniPersister<BasicConfiguration> persister = new IniPersister<BasicConfiguration>(
				new ConfigurationFactory<BasicConfiguration>() {

					@Override
					public BasicConfiguration buildConfiguration(String id) {
						return new BasicConfiguration(id);
					}
				}, temp);
		
		ConfigurationList<BasicConfiguration> settings = persister.read();
		BasicConfiguration config = settings.getConfigurations().get(0);
		
		host = config.getProperty("host");
		username = config.getProperty("username");
		password = config.getProperty("password");

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFileUpload() {
		FTPClient client = new FTPClient();
		FTPProcessor processor = new FTPProcessor(client);

		List<Command> commands = new ArrayList<>();
		
		commands.add(new Connect(host));
		commands.add(new Login(username, password));
		commands.add(new UploadFile(new File("test_upload.txt")));
		
		
		commands.add(new Logout());
		commands.add(new Disconnect());
		processor.addAll(commands);
		
		processor.run();
		
	}

}
