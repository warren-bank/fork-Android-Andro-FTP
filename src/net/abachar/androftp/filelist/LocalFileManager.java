package net.abachar.androftp.filelist;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * 
 * @author abachar
 */
public class LocalFileManager extends FileManager {

	/**
	 * @see net.abachar.androftp.filelist.FileManager#init(java.util.Map)
	 */
	public LocalFileManager(Map<String, String> data) {
		super(data);

		// Initial paths
		rootPath = data.get("local.rootPath");
		String path = data.get("local.currentPath");
		updateListFiles(path);

		// Initial order
		orderByComparator = new OrderByComparator(OrderBy.valueOf(data.get("local.orderBy")));

	}

	/**
	 * @see net.abachar.androftp.filelist.FileManager#goParent()
	 */
	@Override
	public void goParent() {

		// refresh list files
		updateListFiles(paths.pop());
	}

	/**
	 * @see net.abachar.androftp.filelist.FileManager#cwd(java.util.String)
	 */
	@Override
	public void cwd(String name) {

		// Push current path in stack
		paths.push(currentPath);

		// refresh list files
		updateListFiles(currentPath + File.separator + name);
	}

	/**
	 * @see net.abachar.androftp.filelist.FileManager#updateListFiles(java.util.String)
	 */
	@Override
	public void updateListFiles(String path) {

		// Update current path
		currentPath = path;

		// List all files
		File[] list = (new File(currentPath)).listFiles(new FileFilter() {

			@Override
			public boolean accept(File f) {
				return (f.isFile() || f.isDirectory()) && !f.isHidden();
			}
		});

		// Update list files
		files = new ArrayList<FileEntry>();

		// Scan all files
		if ((list != null) && (list.length > 0)) {
			for (File sf : list) {
				FileEntry df = new FileEntry();
				df.setName(sf.getName());
				df.setPath(sf.getAbsolutePath());
				df.setSize(sf.length());
				// df.setType(FileType.getFileType(file));
				df.setLastModified(sf.lastModified());
				files.add(df);
			}
		}

		// Order
		Collections.sort(files, orderByComparator);
	}

	/**
	 * @see net.abachar.androftp.filelist.FileManager#changeOrderBy(net.abachar.androftp.filelist.OrderBy)
	 */
	@Override
	public void changeOrderBy(OrderBy orderBy) {
		orderByComparator.orderBy = orderBy;
		
		// ReOrder
		Collections.sort(files, orderByComparator);
	}
}
