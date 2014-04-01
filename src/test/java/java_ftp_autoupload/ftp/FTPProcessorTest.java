package java_ftp_autoupload.ftp;

import java.util.ArrayList;
import java.util.List;

import java_ftp_autoupload.ftp.command.Command;
import java_ftp_autoupload.ftp.command.impl.Connect;
import java_ftp_autoupload.ftp.command.impl.Disconnect;
import java_ftp_autoupload.ftp.command.impl.Login;
import java_ftp_autoupload.ftp.command.impl.Logout;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FTPProcessorTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		FTPClient client = new FTPClient();
		FTPProcessor processor = new FTPProcessor(client);
		
		// TODO: unit test FTP server
		
		List<Command> commands = new ArrayList<>();
		
		
		processor.addAll(commands);
		
		processor.run();
		
	}

}
