package net.abachar.androftp.filelist.manager;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 
 * @author abachar
 */
public abstract class AbstractFileManager implements FileManager {
	
	/**
	 * 
	 */
	protected Context mContext;
	
	/**
	 * Update list files listeners
	 */
	protected Map<FileManagerEvent, Set<FileManagerListener>> listeners;

	/**
	 * 
	 */
	protected boolean connected;

	/**
	 * Root path
	 */
	protected boolean inRootFolder;

	/**
	 * Root path
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
	public AbstractFileManager(Context context) {
		mContext = context;
		listeners = new HashMap<FileManagerEvent, Set<FileManagerListener>>();
		connected = false;
		inRootFolder = true;
		currentPath = null;
		orderByComparator = new OrderByComparator(OrderBy.NAME);
		files = null;
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#connect()
	 */
	@Override
	public void connect() {

		/** Excute a command asynchronously */
		execAsyncCommand(FileManagerEvent.WILL_CONNECT, FileManagerEvent.DID_CONNECT, new AsyncCommand() {
			public boolean execute() {

				try {
					// Connection
					doConnect();
					connected = true;

					// Notify listeners that the file list is ready
					notifyListeners(FileManagerEvent.INITIAL_LIST_FILES);

					return true;
					// } catch (ConnectionException ex) {
					// connected = false;
					//
					// // Send error connexion and skip end message
					// notifyListeners(FileManagerMessage.ERROR_CONNECTION);
					// return false;
				} catch (FileManagerException ex) {
					connected = false;
					Log.e("AFM", "connect exception", ex);

					// Send error connexion and skip end message
					notifyListeners(FileManagerEvent.ERROR_CONNECTION);
					return false;
				}
			}
		});
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#changeToParentDirectory()
	 */
	@Override
	public void changeToParentDirectory() {

		// Can we go to parent folder
		if (!inRootFolder) {
			/** Excute a command asynchronously */
			execAsyncCommand(FileManagerEvent.WILL_LOAD_LIST_FILES, FileManagerEvent.DID_LOAD_LIST_FILES, new AsyncCommand() {
				public boolean execute() {

					try {
						doChangeToParentDirectory();
						return true;
					} catch (FileManagerException ex) {
						Log.e("AFM", "changeToParentDirectory exception", ex);
						// Send error connexion and skip end message
						notifyListeners(FileManagerEvent.LOST_CONNECTION);
						return false;
					}
				}
			});
		}
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#changeWorkingDirectory(java.util.String)
	 */
	@Override
	public void changeWorkingDirectory(final String dirname) {

		/** Excute a command asynchronously */
		execAsyncCommand(FileManagerEvent.WILL_LOAD_LIST_FILES, FileManagerEvent.DID_LOAD_LIST_FILES, new AsyncCommand() {
			public boolean execute() {

				try {
					doChangeWorkingDirectory(dirname);
					return true;
				} catch (FileManagerException ex) {
					Log.e("AFM", "changeWorkingDirectory exception", ex);
					// Send error connexion and skip end message
					notifyListeners(FileManagerEvent.LOST_CONNECTION);
					return false;
				}
			}
		});
	}

	/**
	 * 
	 */
	public void changeOrderBy(final OrderBy orderBy) {

		// Update order by
		orderByComparator.orderBy = orderBy;

		// Re-order files if needed
		if ((files != null) && !files.isEmpty()) {

			/** Excute a command asynchronously */
			execAsyncCommand(FileManagerEvent.WILL_LOAD_LIST_FILES, FileManagerEvent.DID_LOAD_LIST_FILES, new AsyncCommand() {
				public boolean execute() {
					Collections.sort(files, orderByComparator);
					return true;
				}
			});
		}
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#createNewfolder(java.lang.String)
	 */
	@Override
	public void createNewfolder(final String name) {

		/** Excute a command asynchronously */
		execAsyncCommand(FileManagerEvent.WILL_LOAD_LIST_FILES, FileManagerEvent.DID_LOAD_LIST_FILES, new AsyncCommand() {
			public boolean execute() {

				try {
					doCreateNewfolder(name);
					return true;
				} catch (FileManagerException ex) {
					Log.e("AFM", "refresh exception", ex);
					// Send error connexion and skip end message
					notifyListeners(FileManagerEvent.LOST_CONNECTION);
					return false;
				}
			}
		});
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#refresh()
	 */
	@Override
	public void refresh() {

		/** Excute a command asynchronously */
		execAsyncCommand(FileManagerEvent.WILL_LOAD_LIST_FILES, FileManagerEvent.DID_LOAD_LIST_FILES, new AsyncCommand() {
			public boolean execute() {

				try {
					doRefresh();
					return true;
				} catch (FileManagerException ex) {
					Log.e("AFM", "refresh exception", ex);
					// Send error connexion and skip end message
					notifyListeners(FileManagerEvent.LOST_CONNECTION);
					return false;
				}
			}
		});
	}

	/**
	 * 
	 */
	protected abstract void doConnect() throws FileManagerException;

	/**
	 * 
	 */
	protected abstract void doChangeToParentDirectory() throws FileManagerException;

	/**
	 * 
	 */
	protected abstract void doChangeWorkingDirectory(String dirname) throws FileManagerException;

	/**
	 * 
	 */
	protected abstract void doRefresh() throws FileManagerException;

	/**
	 * 
	 */
	protected abstract void doCreateNewfolder(String name) throws FileManagerException;

	/**
	 * 
	 */
	public void addFileManagerListener(FileManagerListener listener, FileManagerEvent... events) {

		for (FileManagerEvent event : events) {

			if (!listeners.containsKey(event)) {
				listeners.put(event, new HashSet<FileManagerListener>());
			}

			Set<FileManagerListener> l = listeners.get(event);
			if (!l.contains(listener)) {
				l.add(listener);

				// Notify new added listner
				if (event.equals(FileManagerEvent.INITIAL_LIST_FILES)) {
					listener.onFileManagerEvent(this, FileManagerEvent.INITIAL_LIST_FILES);
				}
			}
		}
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#isConnected()
	 */
	@Override
	public boolean isConnected() {
		return connected;
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#canGoParent()
	 */
	@Override
	public boolean canGoParent() {
		return !inRootFolder;
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#getCurrentPath()
	 */
	@Override
	public String getCurrentPath() {
		return currentPath;
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#getFiles()
	 */
	@Override
	public List<FileEntry> getFiles() {
		return files;
	}

	/**
	 * Asynchronous command callback
	 */
	protected static interface AsyncCommand {

		/**
		 * @return true if command executed successfully
		 */
		public boolean execute();
	}

	/**
	 * 
	 * @param beginMsg
	 * @param endMsg
	 * @param asyncCommand
	 */
	protected void execAsyncCommand(final FileManagerEvent beginMsg, final FileManagerEvent endMsg, final AsyncCommand asyncCommand) {

		// Load list files in separate thread
		new Thread(new Runnable() {
			public void run() {

				// Send begin message
				if (beginMsg != null) {
					notifyListeners(beginMsg);
				}

				// Execute the command
				if (asyncCommand.execute()) {

					// Send end message
					if (endMsg != null) {
						notifyListeners(endMsg);
					}
				}
			}
		}).start();
	}

	/**
	 * 
	 * @param fmm
	 */
	protected void notifyListeners(FileManagerEvent fileManagerMessage) {
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
			FileManagerEvent fmm = FileManagerEvent.values()[msg.what];

			if (listeners.containsKey(fmm)) {
				Set<FileManagerListener> l = listeners.get(fmm);
				for (FileManagerListener listener : l) {
					listener.onFileManagerEvent((FileManager) msg.obj, fmm);
				}
			}
		}
	};
}
