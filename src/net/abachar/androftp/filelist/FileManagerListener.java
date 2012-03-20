package net.abachar.androftp.filelist;

/**
 * 
 * @author abachar
 */
public interface FileManagerListener {

	/**
	 * 
	 */
	public void onBeginUpdateListFiles(FileManager fm);

	/**
	 * 
	 */
	public void onEndUpdateListFiles(FileManager fm);
}
