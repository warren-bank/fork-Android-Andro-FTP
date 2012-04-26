package net.abachar.androftp.filelist.manager;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
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
	protected void doConnect() {

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
	 * @see net.abachar.androftp.filelist.manager.FileManager#doChangeWorkingDirectory(net.abachar.androftp.filelist.manager.FileEntry)
	 */
	@Override
	protected void doChangeWorkingDirectory(FileEntry dir) throws FileManagerException {

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
	protected void doDeleteFiles(FileEntry[] files) throws FileManagerException {
		for (FileEntry file : files) {
			File f = new File(file.getAbsolutePath());
			if (!f.delete()) {
				// Toast.makeText(mContext, R.string.err_delete_file,
				// Toast.LENGTH_SHORT).show(); Exception
			}
		}

		// Refresh file list
		loadFiles();
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.AbstractFileManager#doCreateNewfolder(net.abachar.androftp.filelist.manager.FileEntry)
	 */
	@Override
	protected void doCreateNewfolder(FileEntry dir) throws FileManagerException {
		String newFolder = dir.getAbsolutePath();

		File folder = new File(newFolder);
		if (folder.exists()) {
			// Toast.makeText(mContext, R.string.err_folder_already_exists,
			// Toast.LENGTH_SHORT).show(); Exception
		} else {
			if (folder.mkdir()) {
				// Refresh file list
				loadFiles();
			} else {
				// Toast.makeText(mContext, R.string.err_create_folder,
				// Toast.LENGTH_SHORT).show(); Exception
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
	 * @see net.abachar.androftp.filelist.manager.AbstractFileManager#doRenameFile(net.abachar.androftp.filelist.manager.FileEntry,
	 *      net.abachar.androftp.filelist.manager.FileEntry)
	 */
	@Override
	protected void doRenameFile(FileEntry file, FileEntry newFile) throws FileManagerException {

		// New file
		File nfile = new File(newFile.getAbsolutePath());
		if (nfile.exists()) {
			// Toast.makeText(mContext, R.string.err_file_already_exists,
			// Toast.LENGTH_SHORT).show(); Exception
		} else {
			File oldFile = new File(file.getAbsolutePath());
			if (oldFile.renameTo(nfile)) {
				// Refresh file list
				loadFiles();
			} else {
				// Toast.makeText(mContext, R.string.err_rename_file,
				// Toast.LENGTH_SHORT).show(); Exception
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
