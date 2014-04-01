package java_ftp_autoupload.watcher;

import java.io.IOException;
import java.net.URISyntaxException;

import java_ftp_autoupload.watcher.FileWatcher;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FTPUploadTest {

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
	public void test() throws URISyntaxException, IOException {
		
		FileWatcher watcher = new FileWatcher("temp", true);
		watcher.start();
			
	}

}
