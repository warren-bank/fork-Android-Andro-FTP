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

	/** List files changed */
	public final static int WILL_LOAD_LIST_FILES = 0x05;
	public final static int DID_LOAD_LIST_FILES = 0x06;

	/** File management messages */
	public final static int CREATE_FOLDER_SUCCESS = 0x07;		//
	public final static int DELETE_FILE_SUCCESS = 0x08;			//
	public final static int DELETE_FOLDER_SUCCESS = 0x09;
	public final static int RENAME_FILE_SUCCESS = 0x10;			//
	public final static int RENAME_FOLDER_SUCCESS = 0x11;

	/** File management errors */
	public final static int ERR_CREATE_FOLDER = 0x12;			//
	public final static int ERR_DELETE_FILE = 0x13;				//
	public final static int ERR_DELETE_FOLDER = 0x14;			//
	public final static int ERR_RENAME_FILE = 0x15;				//
	public final static int ERR_RENAME_FOLDER = 0x16;			//
	public final static int ERR_FILE_ALREADY_EXISTS = 0x17;		//
	public final static int ERR_FOLDER_ALREADY_EXISTS = 0x18;	//

	/** */
	private FileManager mSource;

	/** Evenement */
	private int mEvent;

	/** */
	public FileManagerEvent(int event) {
		mEvent = event;
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
}
