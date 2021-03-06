package java_ftp_autoupload.watcher;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Expected parameters
 * 
 * Overwrite existing files.
 * 
 * Remove local files.
 * 
 * Settle time.
 * 
 * 
 * 
 * @author Benjamin Leov
 * 
 */

public class FileWatcher implements Runnable {

	private static final Logger logger = LoggerFactory
			.getLogger(FileWatcher.class);
	private final WatchService watcher;
	private final Map<WatchKey, Path> keys;
	private final boolean recursive;
	private boolean trace = false;

	private List<FileWatcherListener> listeners;

	public FileWatcher(String directory, boolean recursive) throws IOException {
		this(Paths.get(new File(directory).toURI()), recursive);
	}

	/**
	 * Creates a WatchService and registers the given directory
	 */
	public FileWatcher(Path dir, boolean recursive) throws IOException {
		this.watcher = FileSystems.getDefault().newWatchService();
		this.keys = new HashMap<WatchKey, Path>();
		this.recursive = recursive;

		this.listeners = new ArrayList<>();

		if (recursive) {
			logger.info("Scanning {} ...", dir);
			registerAll(dir);
		} else {
			register(dir);
		}

		logger.info("Finished scanning");

		// enable trace after initial registration
		this.trace = true;
	}

	@SuppressWarnings("unchecked")
	static <T> WatchEvent<T> cast(WatchEvent<?> event) {
		return (WatchEvent<T>) event;
	}

	/**
	 * Register the given directory with the WatchService
	 */
	private void register(Path dir) throws IOException {
		WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE,
				ENTRY_MODIFY);
		if (trace) {
			Path prev = keys.get(key);
			if (prev == null) {
				logger.info("register: {}", dir);
			} else {
				if (!dir.equals(prev)) {
					logger.info("update: {} -> {}", prev, dir);
				}
			}
		}
		keys.put(key, dir);
	}

	/**
	 * Register the given directory, and all its sub-directories, with the
	 * WatchService.
	 */
	private void registerAll(final Path start) throws IOException {
		// register directory and sub-directories
		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir,
					BasicFileAttributes attrs) throws IOException {
				register(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	/**
	 * Process all events for keys queued to the watcher
	 */
	@Override
	public void run() {
		logger.info("Starting.");
		for (;;) {

			// wait for key to be signalled
			WatchKey key;
			try {
				key = watcher.take();
			} catch (InterruptedException x) {
				x.printStackTrace();
				return;
			}

			Path dir = keys.get(key);
			if (dir == null) {
				logger.error("WatchKey not recognized!!");
				continue;
			}

			for (WatchEvent<?> event : key.pollEvents()) {
				Kind<?> kind = event.kind();

				// TBD - provide example of how OVERFLOW event is handled
				if (kind == OVERFLOW) {
					continue;
				}

				// Context for directory entry event is the file name of entry
				WatchEvent<Path> ev = cast(event);
				Path name = ev.context();
				Path child = dir.resolve(name);

				// print out event
				//logger.info("{}: {}", event.kind().name(), child);
				notifyListeners(kind, new File(child.toUri()));

				// if directory is created, and watching recursively, then
				// register it and its sub-directories
				if (recursive && (kind == ENTRY_CREATE)) {
					try {
						if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
							registerAll(child);
						}
					} catch (IOException x) {
						// ignore to keep sample readable
					}
				}
			}

			// reset key and remove from set if directory no longer accessible
			boolean valid = key.reset();
			if (!valid) {
				keys.remove(key);

				// all directories are inaccessible
				if (keys.isEmpty()) {
					break;
				}
			}
		}
	}

	private void notifyListeners(Kind<?> kind, File file) {

		for (FileWatcherListener curr : listeners) {

			if (kind == ENTRY_CREATE) {
				curr.onFileChange(FileOperation.CREATE, file);
			} else if (kind == ENTRY_DELETE) {
				curr.onFileChange(FileOperation.DELETE, file);
			} else if (kind == ENTRY_MODIFY) {
				curr.onFileChange(FileOperation.MODIFY, file);
			} else {
				curr.onFileChange(FileOperation.UNKNOWN, file);
			}

		}
	}
	
	public void addListener(FileWatcherListener listener) {
		listeners.add(listener);
	}

}
