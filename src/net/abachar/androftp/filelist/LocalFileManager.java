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
	 * @see net.abachar.androftp.filelist.FileManager#init(java.util.Map)
	 */
	public LocalFileManager(Bundle bundle) {
		super(bundle);

		// Initial order
		orderByComparator = new OrderByComparator((OrderBy) bundle.get("local.orderBy"));

		// Initial paths
		rootPath = bundle.getString("local.rootPath");
		String path = bundle.getString("local.currentPath");
		updateListFiles(path);
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
	 * @see net.abachar.androftp.filelist.FileManager#loadFiles()
	 */
	@Override
	protected List<FileEntry> loadFiles() {

		List<FileEntry> files = new ArrayList<FileEntry>();

		// Load local files
		File[] list = (new File(currentPath)).listFiles(new FileFilter() {

			@Override
			public boolean accept(File f) {
				return (f.isFile() || f.isDirectory()) && !f.isHidden();
			}
		});

		// Scan all files
		if ((list != null) && (list.length > 0)) {
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
