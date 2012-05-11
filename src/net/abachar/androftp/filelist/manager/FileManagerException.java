package net.abachar.androftp.filelist.manager;

/**
 * 
 * @author abachar
 */
public class FileManagerException extends Exception {

	/** serialVersionUID */
	private static final long serialVersionUID = 6311192244838839385L;

	/** */
	private int mErrorCode;

	/**
	 * 
	 * @param errorCode
	 */
	public FileManagerException(int errorCode) {
		mErrorCode = errorCode;
	}

	/**
	 * @return the mErrorCode
	 */
	public int getErrorCode() {
		return mErrorCode;
	}
}
