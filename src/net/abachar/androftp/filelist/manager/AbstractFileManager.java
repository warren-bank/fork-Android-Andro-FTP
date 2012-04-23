package net.abachar.androftp.filelist.manager;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 
 * @author abachar
 */
public abstract class AbstractFileManager implements FileManager {

	/** Context */
	protected Context mContext;

	/** File manager listeners */
	protected Map<FileManagerEvent, Set<FileManagerListener>> mListeners;

	/** Connection status */
	protected boolean mConnected;

	/** Root path */
	protected String mRootPath;
	protected boolean mInRootFolder;

	/** Current path */
	protected String mCurrentPath;

	/** Order */
	protected OrderByComparator mOrderByComparator;

	/** List of files and directories */
	protected List<FileEntry> mFileList;

	/**
	 * Default constructor
	 */
	public AbstractFileManager(Context context) {
		mContext = context;
		mListeners = new HashMap<FileManagerEvent, Set<FileManagerListener>>();
		mConnected = false;
		mInRootFolder = true;
		mCurrentPath = null;
		mOrderByComparator = new OrderByComparator(OrderBy.NAME);
		mFileList = null;
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#connect()
	 */
	@Override
	public void connect() {
		new BackgroundOperationTask(BackgroundOperation.CONNECT).execute("");
	}

	/**
	 * Do work on second thread class
	 */
	private class BackgroundOperationTask extends AsyncTask<String, Void, String> {

		/** Work type */
		private BackgroundOperation mOperation;

		/** */
		public BackgroundOperationTask(BackgroundOperation operation) {
			mOperation = operation;
		}

		/**
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			switch (mOperation) {

				case CONNECT:
					notifyListeners(FileManagerEvent.WILL_CONNECT);
					break;
			}
		}

		/**
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected String doInBackground(String... params) {

			try {
				switch (mOperation) {

					case CONNECT:
						doConnect();
						break;
				}
			} catch (ConnectionException ex) {

			} catch (FileManagerException ex) {

			}
			return null;
		}

		/**
		 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
		 */
		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}

		/**
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			switch (mOperation) {

				case CONNECT:
					mConnected = false; // true;

					notifyListeners(FileManagerEvent.ERROR_CONNECTION);
					// notifyListeners(FileManagerEvent.INITIAL_LIST_FILES);
					// notifyListeners(FileManagerEvent.DID_CONNECT);
					break;
			}
		}
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#changeToParentDirectory()
	 */
	@Override
	public void changeToParentDirectory() {

		// Can we go to parent folder
		if (!mInRootFolder) {
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
		mOrderByComparator.orderBy = orderBy;

		// Re-order files if needed
		if ((mFileList != null) && !mFileList.isEmpty()) {

			/** Excute a command asynchronously */
			execAsyncCommand(FileManagerEvent.WILL_LOAD_LIST_FILES, FileManagerEvent.DID_LOAD_LIST_FILES, new AsyncCommand() {
				public boolean execute() {
					Collections.sort(mFileList, mOrderByComparator);
					return true;
				}
			});
		}
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#deleteFiles(java.util.List)
	 */
	@Override
	public void deleteFiles(final List<FileEntry> files) {

		/** Excute a command asynchronously */
		execAsyncCommand(FileManagerEvent.WILL_LOAD_LIST_FILES, FileManagerEvent.DID_LOAD_LIST_FILES, new AsyncCommand() {
			public boolean execute() {

				try {
					doDeleteFiles(files);
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
	 * @see net.abachar.androftp.filelist.manager.FileManager#renameFile(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void renameFile(final String fileName, final String newFileName) {

		/** Excute a command asynchronously */
		execAsyncCommand(FileManagerEvent.WILL_LOAD_LIST_FILES, FileManagerEvent.DID_LOAD_LIST_FILES, new AsyncCommand() {
			public boolean execute() {

				try {
					doRenameFile(fileName, newFileName);
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
	protected abstract void doDeleteFiles(List<FileEntry> files) throws FileManagerException;

	/**
	 * 
	 */
	protected abstract void doCreateNewfolder(String name) throws FileManagerException;

	/**
	 * 
	 */
	protected abstract void doRenameFile(String fileName, String newFileName) throws FileManagerException;

	/**
	 * 
	 */
	public void addFileManagerListener(FileManagerListener listener, FileManagerEvent... events) {

		for (FileManagerEvent event : events) {

			if (!mListeners.containsKey(event)) {
				mListeners.put(event, new HashSet<FileManagerListener>());
			}

			Set<FileManagerListener> l = mListeners.get(event);
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
		return mConnected;
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#canGoParent()
	 */
	@Override
	public boolean canGoParent() {
		return !mInRootFolder;
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#getCurrentPath()
	 */
	@Override
	public String getCurrentPath() {
		return mCurrentPath;
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#getFiles()
	 */
	@Override
	public List<FileEntry> getFiles() {
		return mFileList;
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
		if (!mListeners.isEmpty()) {
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

			if (mListeners.containsKey(fmm)) {
				Set<FileManagerListener> l = mListeners.get(fmm);
				for (FileManagerListener listener : l) {
					listener.onFileManagerEvent((FileManager) msg.obj, fmm);
				}
			}
		}
	};
}
