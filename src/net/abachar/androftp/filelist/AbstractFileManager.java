package net.abachar.androftp.filelist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * 
 * @author abachar
 */
public abstract class AbstractFileManager implements FileManager {

	/**
	 * Root path
	 */
	protected String rootPath;

	/**
	 * Current path
	 */
	protected String currentPath;

	/**
	 * 
	 */
	protected List<FileManagerListener> listeners;

	/**
	 * Order
	 */
	protected OrderByComparator orderByComparator;

	/**
	 * List of files and directories
	 */
	protected List<FileEntry> files;

	/**
	 * Stack paths
	 */
	protected Stack<String> paths;

	/**
	 * Default constructor
	 */
	public AbstractFileManager() {
		// Paths
		paths = new Stack<String>();
		
		// Listners
		listeners = new ArrayList<FileManagerListener>();
		
		// Default order by
		orderByComparator = new OrderByComparator(OrderBy.NAME);
		
		// No files
		files = null;
	}

	/**
	 * @see net.abachar.androftp.filelist.FileManager#init(android.os.Bundle)
	 */
	@Override
	public void init(Bundle bundle) {
	}

	/**
	 * 
	 */
	public abstract void goParent();

	/**
	 * 
	 */
	public abstract void cwd(String name);

	/**
	 *
	 */
	protected void updateListFiles(final boolean reload) {
		final FileManager fm = this;

		// Load list files in separate thread
		new Thread(new Runnable() {
			public void run() {

				// Notify listner
				if (!listeners.isEmpty()) {
					Message msg = handler.obtainMessage();
					msg.what = MSG_BEGIN_UPDATE_LIST_FILES;
					msg.obj = fm;
					handler.sendMessage(msg);
				}

				// List all files
				if (reload) {
					files = loadFiles();
				}

				// Order
				if ((files != null) && !files.isEmpty()) {
					Collections.sort(files, orderByComparator);
				}

				// Notify listner
				if (!listeners.isEmpty()) {
					Message msg = handler.obtainMessage();
					msg.what = MSG_END_UPDATE_LIST_FILES;
					msg.obj = fm;
					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	/**
	 * 
	 */
	protected abstract List<FileEntry> loadFiles();

	/**
	 * 
	 */
	public void setOrderBy(final OrderBy orderBy) {

		// Update order by
		orderByComparator.orderBy = orderBy;

		// refresh list files with same path
		updateListFiles(false);
	}

	/**
	 * 
	 */
	public void addFileManagerListener(FileManagerListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);

			// Notify new added listner
			listener.onEndUpdateListFiles(this);
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean isGoParentEnabled() {
		return !paths.empty();
	}

	/**
	 * @return the files
	 */
	public List<FileEntry> getFiles() {
		return files;
	}
	
	/**
	 * @return the currentPath
	 */
	public String getCurrentPath() {
		return currentPath;
	}

	public static final int MSG_BEGIN_UPDATE_LIST_FILES = 0;
	public static final int MSG_END_UPDATE_LIST_FILES = 1;
	
	/**
	 * 
	 */
	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_BEGIN_UPDATE_LIST_FILES:
					for (FileManagerListener listener : listeners) {
						listener.onBeginUpdateListFiles((FileManager) msg.obj);
					}
					break;
					
				case MSG_END_UPDATE_LIST_FILES:
					for (FileManagerListener listener : listeners) {
						listener.onEndUpdateListFiles((FileManager) msg.obj);
					}
					break;
					
				default:
					break;
			}
		}
	};

	/**
	 * 
	 */
	protected static class OrderByComparator implements Comparator<FileEntry> {

		/** */
		protected OrderBy orderBy;

		/**
		 * 
		 */
		public OrderByComparator(OrderBy orderBy) {
			this.orderBy = orderBy;
		}

		/**
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(FileEntry lhs, FileEntry rhs) {

			// Folder before files
			if (lhs.isFolder() && !rhs.isFolder()) {
				return -1;
			}
			if (!lhs.isFolder() && rhs.isFolder()) {
				return 1;
			}

			// Order by type
			if (orderBy == OrderBy.TYPE) {
				int ret = lhs.getType().compareTo(rhs.getType());
				if (ret != 0) {
					return ret;
				}
			}

			// Order by time
			if (orderBy == OrderBy.TIME) {
				if (lhs.getLastModified() < rhs.getLastModified()) {
					return -1;
				}
				if (lhs.getLastModified() > rhs.getLastModified()) {
					return 1;
				}
			}

			// Order by size
			if (orderBy == OrderBy.SIZE) {
				if (lhs.getSize() < rhs.getSize()) {
					return -1;
				}
				if (lhs.getSize() > rhs.getSize()) {
					return 1;
				}
			}

			// orderBy == ORDER_BY_NAME
			return lhs.getName().compareToIgnoreCase(rhs.getName());
		}
	}
}
