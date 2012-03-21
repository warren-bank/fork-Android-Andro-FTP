package net.abachar.androftp.filelist;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;

/**
 * 
 * @author abachar
 */
public class LocalFileManager extends AbstractFileManager {

	/**
	 * Default constructor
	 */
	public LocalFileManager() {
		super();
	}
	
	/**
	 * @see net.abachar.androftp.filelist.FileManager#init(android.os.Bundle)
	 */
	@Override
	public void init(Bundle bundle) {
		super.init(bundle);

		// Initial order
		if (bundle.containsKey("local.orderBy")) {
			orderByComparator = new OrderByComparator((OrderBy) bundle.get("local.orderBy"));
		}

		// Initial paths
		rootPath = bundle.getString("local.rootPath");
		currentPath = bundle.getString("local.currentPath");

		// Update list files
		updateListFiles(true);
	}

	/**
	 * @see net.abachar.androftp.filelist.FileManager#goParent()
	 */
	@Override
	public void goParent() {

		// refresh list files
		currentPath = paths.pop();
		updateListFiles(true);
	}

	/**
	 * @see net.abachar.androftp.filelist.FileManager#cwd(java.util.String)
	 */
	@Override
	public void cwd(String name) {

		// Push current path in stack
		paths.push(currentPath);

		// refresh list files
		currentPath = currentPath + File.separator + name;
		updateListFiles(true);
	}

	/**
	 * @see net.abachar.androftp.filelist.FileManager#loadFiles()
	 */
	@Override
	protected List<FileEntry> loadFiles() {
		List<FileEntry> files = null;

		// Load local files
		File[] list = (new File(currentPath)).listFiles(new FileFilter() {

			@Override
			public boolean accept(File f) {
				return (f.isFile() || f.isDirectory()) && !f.isHidden();
			}
		});

		// Scan all files
		if ((list != null) && (list.length > 0)) {
			files = new ArrayList<FileEntry>();
			for (File sf : list) {
				FileEntry df = new FileEntry();
				df.setName(sf.getName());
				df.setPath(sf.getAbsolutePath());
				df.setSize(sf.length());
				df.setType(FileType.fromFile(sf));
				df.setLastModified(sf.lastModified());

				files.add(df);
			}
		}

		return files;
	}
}
