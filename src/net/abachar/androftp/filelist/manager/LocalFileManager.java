package net.abachar.androftp.filelist.manager;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;

import net.abachar.androftp.R;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

/**
 * 
 * @author abachar
 */
public class LocalFileManager extends AbstractFileManager {

	/**
	 * Default constructor
	 */
	public LocalFileManager(Context context) {
		super(context);
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#init(android.os.Bundle)
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
	 * @see net.abachar.androftp.filelist.manager.FileManager#doConnect()
	 */
	protected void doConnect() throws FileManagerException {

		// Can read curent directory ?
		File currentDir = new File(currentPath);
		if (currentDir.canRead() && currentDir.isDirectory()) {
			// Load files
			loadFiles();
		}
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#doChangeToParentDirectory()
	 */
	@Override
	protected void doChangeToParentDirectory() throws FileManagerException {

		// Open current directory
		File currentDir = new File(currentPath);

		// Get parent path
		currentPath = currentDir.getParent();

		// refresh list files
		inRootFolder = rootPath.equals(currentPath);
		loadFiles();
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#doChangeWorkingDirectory(java.util.String)
	 */
	@Override
	protected void doChangeWorkingDirectory(String dirname) throws FileManagerException {

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
	 * @see net.abachar.androftp.filelist.manager.AbstractFileManager#doCreateNewfolder(java.lang.String)
	 */
	@Override
	protected void doCreateNewfolder(String name) throws FileManagerException {
		String newFolder = currentPath + File.separator + name;

		File folder = new File(newFolder);
		if (folder.exists()) {
			Toast.makeText(mContext, R.string.err_folder_already_exists, Toast.LENGTH_SHORT).show();
		} else {
			if (folder.mkdir()) {
				// Refresh file list
				loadFiles();
			} else {
				Toast.makeText(mContext, R.string.err_create_folder, Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.AbstractFileManager#doRefresh()
	 */
	@Override
	protected void doRefresh() throws FileManagerException {
		// Refresh file list
		loadFiles();
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
