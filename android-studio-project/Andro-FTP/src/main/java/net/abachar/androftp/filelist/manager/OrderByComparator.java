package net.abachar.androftp.filelist.manager;

import java.util.Comparator;

/**
 * 
 * @author abachar
 * 
 */
public class OrderByComparator implements Comparator<FileEntry> {

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
			int o1 = lhs.getType().getOrder();
			int o2 = rhs.getType().getOrder();
			if (o1 != o2) {
				return (o1 < o2) ? -1 : 1;
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
