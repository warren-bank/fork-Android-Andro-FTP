package net.abachar.androftp.filelist.manager;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class BackgroundOperation {

	/** Operation ids */
	public static final int CONNECT_ID = 0x00;
	public static final int CHANGE_TO_PARENT_DIRECTORY_ID = 0x01;
	public static final int CHANGE_WORKING_DIRECTORY_ID = 0x02;
	public static final int CHANGE_ORDER_BY_ID = 0x03;
	public static final int DELETE_FILES_ID = 0x04;
	public static final int CREATE_NEW_FOLDER_ID = 0x05;
	public static final int RENAME_FILE_ID = 0x06;
	public static final int REFRESH_ID = 0x07;

	/** Operations */
	public static BackgroundOperation CONNECT;
	public static BackgroundOperation CHANGE_TO_PARENT_DIRECTORY;
	public static BackgroundOperation CHANGE_WORKING_DIRECTORY;
	public static BackgroundOperation CHANGE_ORDER_BY;
	public static BackgroundOperation DELETE_FILES;
	public static BackgroundOperation CREATE_NEW_FOLDER;
	public static BackgroundOperation RENAME_FILE;
	public static BackgroundOperation REFRESH;

	/** */
	private int mId;

	/** Begin and end events */
	private List<FileManagerEvent> mBeginEvents = new ArrayList<FileManagerEvent>();
	private List<FileManagerEvent> mEndEvents = new ArrayList<FileManagerEvent>();

	/** Init operation */
	static {
		CHANGE_TO_PARENT_DIRECTORY = new BackgroundOperation(CHANGE_TO_PARENT_DIRECTORY_ID, true);
		CHANGE_WORKING_DIRECTORY = new BackgroundOperation(CHANGE_WORKING_DIRECTORY_ID, true);
		CHANGE_ORDER_BY = new BackgroundOperation(CHANGE_ORDER_BY_ID, true);
		REFRESH = new BackgroundOperation(REFRESH_ID, true);

		// Connect operation
		CONNECT = new BackgroundOperation(CONNECT_ID, false);
		CONNECT.addBeginEvents(FileManagerEvent.WILL_CONNECT);
		CONNECT.addEndEvents(FileManagerEvent.INITIAL_LIST_FILES);
		CONNECT.addEndEvents(FileManagerEvent.DID_CONNECT);

		// Delete files
		DELETE_FILES = new BackgroundOperation(DELETE_FILES_ID, true);
		DELETE_FILES.addEndEvents(FileManagerEvent.DELETE_FILE_SUCCESS);

		// Create new folder
		CREATE_NEW_FOLDER = new BackgroundOperation(DELETE_FILES_ID, true);
		CREATE_NEW_FOLDER.addEndEvents(FileManagerEvent.CREATE_FOLDER_SUCCESS);

		// Rename file
		RENAME_FILE = new BackgroundOperation(RENAME_FILE_ID, true);
		RENAME_FILE.addEndEvents(FileManagerEvent.RENAME_FILE_SUCCESS);
	}

	/**
	 * 
	 * @param id
	 */
	private BackgroundOperation(int id, boolean addLoadListFilesEvents) {
		mId = id;
		if (addLoadListFilesEvents) {
			addBeginEvents(FileManagerEvent.WILL_LOAD_LIST_FILES);
			addEndEvents(FileManagerEvent.DID_LOAD_LIST_FILES);
		}
	}

	/**
	 * 
	 * @param initialListFiles
	 */
	private void addBeginEvents(int event) {
		mBeginEvents.add(new FileManagerEvent(event));
	}

	/**
	 * 
	 * @param initialListFiles
	 */
	private void addEndEvents(int event) {
		mEndEvents.add(new FileManagerEvent(event));
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return mId;
	}

	/**
	 * @return the beginEvents
	 */
	public List<FileManagerEvent> getBeginEvents() {
		return mBeginEvents;
	}

	/**
	 * @return the endEvents
	 */
	public List<FileManagerEvent> getEndEvents() {
		return mEndEvents;
	}
}
