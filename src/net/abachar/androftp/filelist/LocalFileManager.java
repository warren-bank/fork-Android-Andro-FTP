package net.abachar.androftp.filelist;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;

import android.os.Bundle;
import android.os.Environment;

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

		// Initial order
		if (bundle.containsKey("local.orderBy")) {
			orderByComparator = new OrderByComparator((OrderBy) bundle.get("local.orderBy"));
		}

		// Initial paths
		if (bundle.containsKey("local.rootPath")) {
			rootPath = bundle.getString("local.rootPath");
		} else {
			rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		}

		if (bundle.containsKey("local.currentPath")) {
			currentPath = bundle.getString("local.currentPath");
		} else {
			currentPath = rootPath;
		}

		inRootFolder = rootPath.equals(currentPath);
	}

	/**
	 * @see net.abachar.androftp.filelist.FileManager#doConnect()
	 */
	protected boolean doConnect() {

		// Can read curent directory ?
		File currentDir = new File(currentPath);
		if (currentDir.canRead() && currentDir.isDirectory()) {
			// Load files
			loadFiles();
			notifyListeners(FileManagerMessage.INITIAL_LIST_FILES);

			// Ok
			return true;
		}

		return false;
	}

	/**
	 * @see net.abachar.androftp.filelist.FileManager#doChangeToParentDirectory()
	 */
	@Override
	protected void doChangeToParentDirectory() {

		// Open current directory
		File currentDir = new File(currentPath);

		// Get parent path
		currentPath = currentDir.getParent();

		// refresh list files
		inRootFolder = rootPath.equals(currentPath);
		loadFiles();
	}

	/**
	 * @see net.abachar.androftp.filelist.FileManager#doChangeWorkingDirectory(java.util.String)
	 */
	@Override
	protected void doChangeWorkingDirectory(String dirname) {

		// Change working directory
		File dir = new File(currentPath + File.separator + dirname);
		if (dir.canRead() && dir.isDirectory()) {
			currentPath = dir.getAbsolutePath();

			// refresh list files
			inRootFolder = false;
			loadFiles();
		}
	}

	/**
	 * 
	 */
	private void loadFiles() {
		files = null;

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
			
			// Sort
			Collections.sort(files, orderByComparator);
		}
	}
}
