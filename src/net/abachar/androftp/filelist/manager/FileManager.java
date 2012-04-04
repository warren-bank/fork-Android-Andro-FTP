package net.abachar.androftp.filelist.manager;

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
	 * 
	 */
	public void connect();

	/**
	 * 
	 */
	public boolean isConnected();

	/**
	 * Install listener for messages
	 */
	public void addFileManagerListener(FileManagerListener listener, FileManagerEvent... messages);

	/**
	 * Go to parent directory
	 */
	public void changeToParentDirectory();

	/**
	 * Change working directory
	 */
	public void changeWorkingDirectory(String dirname);

	/**
	 * Change files order by
	 */
	public void changeOrderBy(final OrderBy orderBy);

	/**
	 * @return true if can go parent directory
	 */
	public boolean canGoParent();

	/**
	 * @return the current path
	 */
	public String getCurrentPath();

	/**
	 * @return files list
	 */
	public List<FileEntry> getFiles();

	/**
	 * 
	 * @param name
	 */
	public void createNewfolder(String name);

	/**
	 * 
	 */
	public void refresh();
}
