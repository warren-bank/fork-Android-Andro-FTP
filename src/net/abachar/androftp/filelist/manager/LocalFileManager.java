package net.abachar.androftp.filelist.manager;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;

import net.abachar.androftp.util.FileType;

import android.os.Bundle;
import android.os.Environment;

/**
 * 
 * @author abachar
 */
public class LocalFileManager extends AbstractFileManager {

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
	protected void doConnect(BackgroundOperationListener listener) {

		// Can read curent directory ?
		File currentDir = new File(mCurrentPath);
		if (currentDir.canRead() && currentDir.isDirectory()) {
			// Load files
			loadFiles();
		}

		mConnected = true;
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#doChangeToParentDirectory()
	 */
	@Override
	protected void doChangeToParentDirectory(BackgroundOperationListener listener) throws FileManagerException {

		// Open current directory
		File currentDir = new File(mCurrentPath);

		// Get parent path
		mCurrentPath = currentDir.getParent();

		// refresh list files
		mInRootFolder = mRootPath.equals(mCurrentPath);
		loadFiles();
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#doChangeWorkingDirectory(net.abachar.androftp.filelist.manager.FileEntry)
	 */
	@Override
	protected void doChangeWorkingDirectory(BackgroundOperationListener listener, FileEntry dir) throws FileManagerException {

		// Change working directory
		File d = new File(dir.getAbsolutePath());
		if (d.canRead() && d.isDirectory()) {
			mCurrentPath = d.getAbsolutePath();

			// refresh list files
			mInRootFolder = false;
			loadFiles();
		}
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.AbstractFileManager#doDeleteFiles(net.abachar.androftp.filelist.manager.FileEntry[])
	 */
	@Override
	protected void doDeleteFiles(BackgroundOperationListener listener, FileEntry[] files) throws FileManagerException {
		for (FileEntry file : files) {
			File f = new File(file.getAbsolutePath());
			if (!f.delete()) {
				if (file.isFolder()) {
					throw new FileManagerException(FileManagerEvent.ERR_DELETE_FOLDER);
				} else {
					throw new FileManagerException(FileManagerEvent.ERR_DELETE_FILE);
				}
			}
		}

		loadFiles();
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.AbstractFileManager#doCreateNewfolder(net.abachar.androftp.filelist.manager.FileEntry)
	 */
	@Override
	protected void doCreateNewfolder(BackgroundOperationListener listener, FileEntry dir) throws FileManagerException {
		String newFolder = dir.getAbsolutePath();

		File folder = new File(newFolder);
		if (folder.mkdir()) {
			loadFiles();
		} else {
			throw new FileManagerException(FileManagerEvent.ERR_CREATE_FOLDER);
		}
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.AbstractFileManager#doRefresh()
	 */
	@Override
	protected void doRefresh(BackgroundOperationListener listener) throws FileManagerException {
		loadFiles();
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.AbstractFileManager#doRenameFile(net.abachar.androftp.filelist.manager.FileEntry,
	 *      net.abachar.androftp.filelist.manager.FileEntry)
	 */
	@Override
	protected void doRenameFile(BackgroundOperationListener listener, FileEntry file, FileEntry newFile) throws FileManagerException {

		// New file
		File nfile = new File(newFile.getAbsolutePath());
		File oldFile = new File(file.getAbsolutePath());
		if (oldFile.renameTo(nfile)) {
			loadFiles();
		} else {
			if (file.isFolder()) {
				throw new FileManagerException(FileManagerEvent.ERR_RENAME_FOLDER);
			} else {
				throw new FileManagerException(FileManagerEvent.ERR_RENAME_FILE);
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
