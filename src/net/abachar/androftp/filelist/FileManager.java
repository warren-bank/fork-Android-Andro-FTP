package net.abachar.androftp.filelist;

import java.util.List;

/**
 * 
 * @author abachar
 */
public interface FileManager {

	/**
	 * 
	 */
	public void addFileManagerListener(FileManagerListener listener);

	/**
	 * 
	 */
	public void goParent();

	/**
	 * 
	 */
	public void cwd(String name);

	/**
	 * 
	 */
	public void setOrderBy(final OrderBy orderBy);

	/**
	 * 
	 * @return
	 */
	public boolean isGoParentEnabled();

	/**
	 * @return the files
	 */
	public List<FileEntry> getFiles();
	
	/**
	 * @return the currentPath
	 */
	public String getCurrentPath();
}
