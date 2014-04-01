package java_ftp_autoupload.watcher;

import java.io.File;

public interface FileWatcherListener {

	void onFileChange(FileOperation operation, File file);
	
}
