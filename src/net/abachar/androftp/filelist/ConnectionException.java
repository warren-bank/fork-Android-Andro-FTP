package net.abachar.androftp.filelist;

/**
 * 
 * @author abachar
 */
public class ConnectionException extends FileManagerException {

	/** serialVersionUID */
	private static final long serialVersionUID = -8876415829351997774L;

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param message the detail message.
	 */
	public ConnectionException(String message) {
		super(message);
	}

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * 
	 * @param message the detail message
	 * @param cause the cause 
	 */
	public ConnectionException(String message, Throwable cause) {
		super(message, cause);
	}
}
