package net.abachar.androftp.filelist;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.os.Handler;
import android.os.Message;

/**
 * 
 * @author abachar
 */
public abstract class AbstractFileManager implements FileManager {

	/**
	 * Update list files listeners
	 */
	protected Set<FileManagerListener> listeners;

	/**
	 * 
	 */
	protected boolean connected;

	/**
	 * Root path
	 */
	protected boolean inRootFolder;

	/**
	 * Current path
	 */
	protected String rootPath;

	/**
	 * Current path
	 */
	protected String currentPath;

	/**
	 * Order
	 */
	protected OrderByComparator orderByComparator;

	/**
	 * List of files and directories
	 */
	protected List<FileEntry> files;

	/**
	 * Default constructor
	 */
	public AbstractFileManager() {
		listeners = new HashSet<FileManagerListener>();
		connected = false;
		inRootFolder = true;
		currentPath = null;
		orderByComparator = new OrderByComparator(OrderBy.NAME);
		files = null;
	}

	/**
	 * @see net.abachar.androftp.filelist.FileManager#connect()
	 */
	@Override
	public void connect() {

		/** Excute a command asynchronously */
		execAsyncCommand(FileManagerMessage.BEGIN_CONNECT, FileManagerMessage.END_CONNECT, new AsyncCommand() {
			public void execute() {
				connected = doConnect();
			}
		});
	}

	/**
	 * 
	 */
	protected abstract boolean doConnect();

	/**
	 * @see net.abachar.androftp.filelist.FileManager#isConnected()
	 */
	@Override
	public boolean isConnected() {
		return connected;
	}

	/**
	 * @see net.abachar.androftp.filelist.FileManager#changeToParentDirectory()
	 */
	@Override
	public void changeToParentDirectory() {

		// Can we go to parent folder 
		if (!inRootFolder) {
			/** Excute a command asynchronously */
			execAsyncCommand(FileManagerMessage.BEGIN_UPDATE_LIST_FILES, FileManagerMessage.END_UPDATE_LIST_FILES, new AsyncCommand() {
				public void execute() {
					doChangeToParentDirectory();
				}
			});
		}
	}
	
	/**
	 * 
	 */
	protected abstract void doChangeToParentDirectory();

	/**
	 * @see net.abachar.androftp.filelist.FileManager#changeWorkingDirectory(java.util.String)
	 */
	@Override
	public void changeWorkingDirectory(final String dirname) {

		/** Excute a command asynchronously */
		execAsyncCommand(FileManagerMessage.BEGIN_UPDATE_LIST_FILES, FileManagerMessage.END_UPDATE_LIST_FILES, new AsyncCommand() {
			public void execute() {
				doChangeWorkingDirectory(dirname);
			}
		});
	}
	
	/**
	 * 
	 */
	protected abstract void doChangeWorkingDirectory(String dirname);
	
	/**
	 * 
	 */
	public void addFileManagerListener(FileManagerListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);

			// Notify new added listner
			listener.onUpdateListFiles(this, FileManagerMessage.INITIAL_LIST_FILES);
		}
	}

	/**
	 * 
	 */
	public void setOrderBy(final OrderBy orderBy) {

		// Update order by
		orderByComparator.orderBy = orderBy;

		// Re-order files if needed
		if ((files != null) && !files.isEmpty()) {
			/** Excute a command asynchronously */
			execAsyncCommand(FileManagerMessage.BEGIN_UPDATE_LIST_FILES, FileManagerMessage.END_UPDATE_LIST_FILES, new AsyncCommand() {
				public void execute() {
					Collections.sort(files, orderByComparator);
				}
			});
		}
	}

	/**
	 * @see net.abachar.androftp.filelist.FileManager#canGoParent()
	 */
	@Override
	public boolean canGoParent() {
		return !inRootFolder;
	}

	/**
	 * @see net.abachar.androftp.filelist.FileManager#getCurrentPath()
	 */
	@Override
	public String getCurrentPath() {
		return currentPath;
	}

	/**
	 * @see net.abachar.androftp.filelist.FileManager#getFiles()
	 */
	@Override
	public List<FileEntry> getFiles() {
		return files;
	}

	/**
	 * 
	 */
	protected interface AsyncCommand {

		/**
		 * 
		 */
		public void execute();
	}

	/**
	 * 
	 * @param beginMsg
	 * @param endMsg
	 * @param asyncCommand
	 */
	protected void execAsyncCommand(final FileManagerMessage beginMsg, final FileManagerMessage endMsg, final AsyncCommand asyncCommand) {

		// Load list files in separate thread
		new Thread(new Runnable() {
			public void run() {

				// Send begin message
				if (beginMsg != null) {
					notifyListeners(beginMsg);
				}

				// Execute the command
				asyncCommand.execute();

				// Send end message
				if (endMsg != null) {
					notifyListeners(endMsg);
				}
			}
		}).start();
	}

	/**
	 * 
	 * @param fmm
	 */
	protected void notifyListeners(FileManagerMessage fileManagerMessage) {
		if (!listeners.isEmpty()) {
			Message msg = handler.obtainMessage();
			msg.what = fileManagerMessage.ordinal();
			msg.obj = this;
			handler.sendMessage(msg);
		}
	}

	/**
	 * 
	 */
	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			for (FileManagerListener listener : listeners) {
				listener.onUpdateListFiles((FileManager) msg.obj, FileManagerMessage.values()[msg.what]);
			}
		}
	};
}
