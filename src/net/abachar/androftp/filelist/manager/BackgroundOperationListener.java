package net.abachar.androftp.filelist.manager;

/**
 * 
 * @author abachar
 */
public interface BackgroundOperationListener {

	/**
	 * 
	 */
	public void onPublishProgress(FileManagerEvent... values);
}
