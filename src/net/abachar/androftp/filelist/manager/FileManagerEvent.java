package net.abachar.androftp.filelist.manager;

/**
 * 
 * @author abachar
 */
public class FileManagerEvent {

	/** Connection messages */
	public final static int WILL_CONNECT = 0x00;
	public final static int DID_CONNECT = 0x01;
	public final static int ERR_CONNECTION = 0x02;
	public final static int ERR_LOST_CONNECTION = 0x03;
	public final static int INITIAL_LIST_FILES = 0x04;
	public final static int LOG_CONNECT = 0x05;

	/** List files changed */
	public final static int WILL_LOAD_LIST_FILES = 0x10;
	public final static int DID_LOAD_LIST_FILES = 0x11;

	/** File management messages */
	public final static int CREATE_FOLDER_SUCCESS = 0x20; //
	public final static int DELETE_FILE_SUCCESS = 0x21; //
	public final static int DELETE_FOLDER_SUCCESS = 0x22;
	public final static int RENAME_FILE_SUCCESS = 0x23; //
	public final static int RENAME_FOLDER_SUCCESS = 0x24;

	/** File management errors */
	public final static int ERR_CREATE_FOLDER = 0x30; //
	public final static int ERR_DELETE_FILE = 0x31; //
	public final static int ERR_DELETE_FOLDER = 0x32; //
	public final static int ERR_RENAME_FILE = 0x33; //
	public final static int ERR_RENAME_FOLDER = 0x34; //
	public final static int ERR_FILE_ALREADY_EXISTS = 0x35; //
	public final static int ERR_FOLDER_ALREADY_EXISTS = 0x36; //

	/** */
	private FileManager mSource;

	/** Evenement */
	private int mEvent;

	/** Message details */
	private String mMessage;

	/** */
	public FileManagerEvent(int event) {
		this(event, null);
	}

	/** */
	public FileManagerEvent(int event, String message) {
		mEvent = event;
		mMessage = message;
	}

	/**
	 * @return the mSource
	 */
	public FileManager getSource() {
		return mSource;
	}

	/**
	 * @param mSource
	 *            the mSource to set
	 */
	public void setSource(FileManager mSource) {
		this.mSource = mSource;
	}

	/**
	 * @return the mEvent
	 */
	public int getEvent() {
		return mEvent;
	}

	/**
	 * @return the mMessage
	 */
	public String getMessage() {
		return mMessage;
	}

	/**
	 * @param mMessage
	 *            the mMessage to set
	 */
	public void setMessage(String mMessage) {
		this.mMessage = mMessage;
	}
}
