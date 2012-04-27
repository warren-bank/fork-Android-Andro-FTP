package net.abachar.androftp.filelist.manager;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.os.AsyncTask;

/**
 * 
 * @author abachar
 */
public abstract class AbstractFileManager implements FileManager {

	/** Context */
	protected Context mContext;

	/** File manager listeners */
	protected Set<FileManagerListener> mListeners;

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
		mListeners = new HashSet<FileManagerListener>();
		mConnected = false;
		mInRootFolder = true;
		mCurrentPath = null;
		mOrderByComparator = new OrderByComparator(OrderBy.NAME);
		mFileList = null;
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
	 * @see net.abachar.androftp.filelist.manager.FileManager#addListener(net.abachar.androftp.filelist.manager.FileManagerListener)
	 */
	@Override
	public void addListener(FileManagerListener listener) {
		if (!mListeners.contains(listener)) {
			mListeners.add(listener);

			// Notify new added listner
			listener.onFileManagerEvent(this, FileManagerEvent.INITIAL_LIST_FILES);
		}
	}

	/**
	 * 
	 * @param fmm
	 */
	protected void notifyListeners(FileManagerEvent event) {
		if (!mListeners.isEmpty()) {
			for (FileManagerListener listener : mListeners) {
				listener.onFileManagerEvent(this, event);
			}
		}
	}

	/**
	 * 
	 * @param fmm
	 */
	protected void notifyListeners(List<FileManagerEvent> events) {
		for (FileManagerEvent event : events) {
			notifyListeners(event);
		}
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#connect()
	 */
	@Override
	public void connect() {
		new BackgroundOperationTask(BackgroundOperation.CONNECT).execute();
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#changeToParentDirectory()
	 */
	@Override
	public void changeToParentDirectory() {
		// Can we go to parent folder
		if (!mInRootFolder) {
			new BackgroundOperationTask(BackgroundOperation.CHANGE_TO_PARENT_DIRECTORY).execute();
		}
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#changeWorkingDirectory(java.util.String)
	 */
	@Override
	public void changeWorkingDirectory(final String dirname) {
		FileEntry dir = null;
		for (FileEntry fileEntry : mFileList) {
			if (dirname.equals(fileEntry.getName())) {
				dir = fileEntry;
				break;
			}
		}

		new BackgroundOperationTask(BackgroundOperation.CHANGE_WORKING_DIRECTORY).execute(dir);
	}

	/**
	 * 
	 */
	public void changeOrderBy(final OrderBy orderBy) {

		// Update order by
		mOrderByComparator.orderBy = orderBy;

		// Re-order files if needed
		if ((mFileList != null) && !mFileList.isEmpty()) {
			new BackgroundOperationTask(BackgroundOperation.CHANGE_ORDER_BY).execute();
		}
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#deleteFiles(java.util.List)
	 */
	@Override
	public void deleteFiles(final List<FileEntry> files) {

		FileEntry[] aFiles = new FileEntry[files.size()];
		files.toArray(aFiles);

		new BackgroundOperationTask(BackgroundOperation.DELETE_FILES).execute(aFiles);
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#createNewfolder(java.lang.String)
	 */
	@Override
	public void createNewfolder(final String dirname) {

		// Check if file already existe
		if ((mFileList != null) && !mFileList.isEmpty()) {
			String d = dirname.toLowerCase();
			for (FileEntry fileEntry : mFileList) {
				if (d.equals(fileEntry.getName().toLowerCase())) {
					// R.string.err_file_already_exists, Exception
				}
			}
		}

		FileEntry dir = new FileEntry(dirname);
		dir.setAbsolutePath(mCurrentPath + File.separator + dirname);
		new BackgroundOperationTask(BackgroundOperation.CREATE_NEW_FOLDER).execute(dir);
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#renameFile(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void renameFile(final String fileName, final String newFileName) {

		String newFileNameLowerCase = newFileName.toLowerCase();
		boolean isSameName = newFileNameLowerCase.equals(fileName.toLowerCase());

		// Old file aheck if a file with the same name already existe
		FileEntry file = null;
		for (FileEntry fileEntry : mFileList) {

			if ((file == null) && fileName.equals(fileEntry.getName())) {
				file = fileEntry;
			}

			if (!isSameName) {
				if (newFileNameLowerCase.equals(fileEntry.getName().toLowerCase())) {
					// R.string.err_file_already_exists, Exception
				}
			}
		}

		// New file
		FileEntry newFile = new FileEntry(newFileName);
		newFile.setAbsolutePath(mCurrentPath + File.separator + newFileName);

		new BackgroundOperationTask(BackgroundOperation.RENAME_FILE).execute(file, newFile);
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#refresh()
	 */
	@Override
	public void refresh() {
		new BackgroundOperationTask(BackgroundOperation.REFRESH).execute();
	}

	/**
	 * Do work on second thread class
	 */
	private class BackgroundOperationTask extends AsyncTask<FileEntry, Void, BackgroundOperationResult> {

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

			// Send begin events
			notifyListeners(mOperation.getBeginEvents());
		}

		/**
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected BackgroundOperationResult doInBackground(FileEntry... params) {
			BackgroundOperationResult result = new BackgroundOperationResult();

			try {
				switch (mOperation.getId()) {

					case BackgroundOperation.CONNECT_ID:
						doConnect();
						break;

					case BackgroundOperation.CHANGE_TO_PARENT_DIRECTORY_ID:
						doChangeToParentDirectory();
						break;

					case BackgroundOperation.CHANGE_WORKING_DIRECTORY_ID:
						doChangeWorkingDirectory(params[0]);
						break;

					case BackgroundOperation.CHANGE_ORDER_BY_ID:
						Collections.sort(mFileList, mOrderByComparator);
						break;

					case BackgroundOperation.DELETE_FILES_ID:
						doDeleteFiles(params);
						break;

					case BackgroundOperation.CREATE_NEW_FOLDER_ID:
						doCreateNewfolder(params[0]);
						break;

					case BackgroundOperation.RENAME_FILE_ID:
						doRenameFile(params[0], params[1]);
						break;

					case BackgroundOperation.REFRESH_ID:
						doRefresh();
						break;
				}

				result.setSuccess(true);
			} catch (ConnectionException ex) {
				result.setSuccess(false);
				result.addReplacementEvent(FileManagerEvent.ERROR_CONNECTION);

			} catch (FileManagerException ex) {
				result.setSuccess(false);
			}
			return result;
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
		protected void onPostExecute(BackgroundOperationResult result) {
			super.onPostExecute(result);

			if (result.isSuccess()) {
				// Send normal end events
				notifyListeners(mOperation.getEndEvents());
			} else {
				if (!result.getReplacementEvents().isEmpty()) {
					notifyListeners(result.getReplacementEvents());
				}
			}
		}
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
	protected abstract void doChangeWorkingDirectory(FileEntry dir) throws FileManagerException;

	/**
	 * 
	 */
	protected abstract void doRefresh() throws FileManagerException;

	/**
	 * 
	 */
	protected abstract void doDeleteFiles(FileEntry[] files) throws FileManagerException;

	/**
	 * 
	 */
	protected abstract void doCreateNewfolder(FileEntry dir) throws FileManagerException;

	/**
	 * 
	 */
	protected abstract void doRenameFile(FileEntry file, FileEntry newFile) throws FileManagerException;
}
