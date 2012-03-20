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
	 * @see net.abachar.androftp.filelist.AbstractFileManager#init(java.util.Map)
	 */
	public AbstractFileManager(Bundle bundle) {
		paths = new Stack<String>();
		listeners = new ArrayList<FileManagerListener>();
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
	protected void updateListFiles(final String path) {
		final FileManager fm = this;

		// Load list files in separate thread
		new Thread(new Runnable() {
			public void run() {

				// Update current path
				boolean reload;
				if (path.equals(currentPath)) {
					reload = false;
				} else {
					currentPath = path;
					reload = true;
				}

				// Notify listner
				if (!listeners.isEmpty()) {
					Message msg = handler.obtainMessage();
					msg.what = MSG_BEGIN_UPDATE_LIST_FILES;
					msg.obj = fm;
					handler.sendMessage(msg);
				}

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
				}

				// List all files
				if (reload) {
					files = loadFiles();
				}

				// Order
				Collections.sort(files, orderByComparator);

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
		updateListFiles(currentPath);
	}

	/**
	 * 
	 */
	public void addFileManagerListener(FileManagerListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
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
				return lhs.getType().compareTo(rhs.getType());
			}

			// Order by time
			if (orderBy == OrderBy.TIME) {
				if (lhs.getLastModified() > rhs.getLastModified()) {
					return 1;
				}
				if (lhs.getLastModified() < rhs.getLastModified()) {
					return -1;
				}
			}

			// Order by size
			if (orderBy == OrderBy.SIZE) {
				return (lhs.getSize() < rhs.getSize() ? -1 : (lhs.getSize() == rhs.getSize() ? 0 : 1));
			}

			// orderBy == ORDER_BY_NAME
			return lhs.getName().compareToIgnoreCase(rhs.getName());
		}
	}
}
