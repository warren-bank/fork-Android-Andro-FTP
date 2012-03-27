package net.abachar.androftp.filelist;

/**
 * 
 * @author abachar
 */
public class FileManagerException extends Exception {

	/** serialVersionUID */
	private static final long serialVersionUID = 6311192244838839385L;

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param message the detail message.
	 */
	public FileManagerException(String message) {
		super(message);
	}

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * 
	 * @param message the detail message
	 * @param cause the cause 
	 */
	public FileManagerException(String message, Throwable cause) {
		super(message, cause);
	}
}
