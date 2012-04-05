package net.abachar.androftp.filelist.manager;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
			mOrderByComparator = new OrderByComparator((OrderBy) bundle.get("local.orderBy"));
		}

		// Initial paths
		if (bundle.containsKey("local.rootPath")) {
			mRootPath = bundle.getString("local.rootPath");
		} else {
			mRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		}

		if (bundle.containsKey("local.currentPath")) {
			mCurrentPath = bundle.getString("local.currentPath");
		} else {
			mCurrentPath = mRootPath;
		}

		mInRootFolder = mRootPath.equals(mCurrentPath);
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#doConnect()
	 */
	protected void doConnect() throws FileManagerException {

		// Can read curent directory ?
		File currentDir = new File(mCurrentPath);
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
		File currentDir = new File(mCurrentPath);

		// Get parent path
		mCurrentPath = currentDir.getParent();

		// refresh list files
		mInRootFolder = mRootPath.equals(mCurrentPath);
		loadFiles();
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#doChangeWorkingDirectory(java.util.String)
	 */
	@Override
	protected void doChangeWorkingDirectory(String dirname) throws FileManagerException {

		// Change working directory
		File dir = new File(mCurrentPath + File.separator + dirname);
		if (dir.canRead() && dir.isDirectory()) {
			mCurrentPath = dir.getAbsolutePath();

			// refresh list files
			mInRootFolder = false;
			loadFiles();
		}
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.AbstractFileManager#doDeleteFiles(java.util.List)
	 */
	@Override
	protected void doDeleteFiles(List<FileEntry> files) throws FileManagerException {

		for (FileEntry fileEntry : files) {
			File file = new File(fileEntry.getAbsolutePath());
			if (!file.delete()) {
				Toast.makeText(mContext, R.string.err_delete_file, Toast.LENGTH_SHORT).show();
			}
		}
		
		// Refresh file list
		loadFiles();
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.AbstractFileManager#doCreateNewfolder(java.lang.String)
	 */
	@Override
	protected void doCreateNewfolder(String name) throws FileManagerException {
		String newFolder = mCurrentPath + File.separator + name;

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
	 * @see net.abachar.androftp.filelist.manager.AbstractFileManager#doRenameFile(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	protected void doRenameFile(String fileName, String newFileName) throws FileManagerException {

		// New file
		File newFile = new File(mCurrentPath + File.separator + newFileName);
		if (newFile.exists()) {
			Toast.makeText(mContext, R.string.err_file_already_exists, Toast.LENGTH_SHORT).show();
		} else {
			File oldFile = new File(mCurrentPath + File.separator + fileName);
			if (oldFile.renameTo(newFile)) {
				// Refresh file list
				loadFiles();
			} else {
				Toast.makeText(mContext, R.string.err_rename_file, Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * 
	 */
	private void loadFiles() {
		mFileList = null;

		// Load local files
		File[] list = (new File(mCurrentPath)).listFiles(new FileFilter() {

			@Override
			public boolean accept(File f) {
				return (f.isFile() || f.isDirectory()) && !f.isHidden();
			}
		});

		// Scan all files
		if ((list != null) && (list.length > 0)) {
			mFileList = new ArrayList<FileEntry>();
			for (File sf : list) {
				FileEntry df = new FileEntry();
				df.setName(sf.getName());
				df.setAbsolutePath(sf.getAbsolutePath());
				df.setParentPath(mCurrentPath);
				df.setSize(sf.length());
				df.setType(FileType.fromFile(sf));
				df.setLastModified(sf.lastModified());

				mFileList.add(df);
			}

			// Sort
			Collections.sort(mFileList, mOrderByComparator);
		}
	}
}
