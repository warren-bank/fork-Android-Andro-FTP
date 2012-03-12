package net.abachar.androftp.filelist;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * 
 * @author abachar
 */
public abstract class FileManager {

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
	 * Stack paths
	 */
	protected Stack<String> paths;

	/**
	 * @see net.abachar.androftp.filelist.FileManager#init(java.util.Map)
	 */
	public FileManager(Map<String, String> data) {
		// Stack
		paths = new Stack<String>();
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
	public abstract void updateListFiles(String path);

	/**
	 * 
	 */
	public abstract void changeOrderBy(OrderBy orderBy);

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
			if (lhs.isDirectory() && !rhs.isDirectory()) {
				return -1;
			}
			if (!lhs.isDirectory() && rhs.isDirectory()) {
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
