package net.abachar.androftp.filelist;

import java.util.List;

import android.os.Bundle;

/**
 * 
 * @author abachar
 */
public interface FileManager {

	/**
	 * Initialize file manager
	 */
	public void init(Bundle bundle);

	/**
	 * Install listner
	 */
	public void addFileManagerListener(FileManagerListener listener);

	/**
	 * Go to parent directory
	 */
	public void goParent();

	/**
	 * Change working directory
	 */
	public void cwd(String name);

	/**
	 * Change files order by
	 */
	public void setOrderBy(final OrderBy orderBy);

	/**
	 * @return true if can go parent directory
	 */
	public boolean isGoParentEnabled();

	/**
	 * @return files list
	 */
	public List<FileEntry> getFiles();

	/**
	 * @return the current path
	 */
	public String getCurrentPath();
}
