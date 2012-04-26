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
		// Connect operation
		CONNECT = new BackgroundOperation();
		CONNECT.mId = CONNECT_ID;
		CONNECT.mBeginEvents.add(FileManagerEvent.WILL_CONNECT);
		CONNECT.mEndEvents.add(FileManagerEvent.INITIAL_LIST_FILES);
		CONNECT.mEndEvents.add(FileManagerEvent.DID_CONNECT);

		// Change to parent directory operation
		CHANGE_TO_PARENT_DIRECTORY = new BackgroundOperation();
		CHANGE_TO_PARENT_DIRECTORY.mId = CHANGE_TO_PARENT_DIRECTORY_ID;
		CHANGE_TO_PARENT_DIRECTORY.mBeginEvents.add(FileManagerEvent.WILL_LOAD_LIST_FILES);
		CHANGE_TO_PARENT_DIRECTORY.mEndEvents.add(FileManagerEvent.DID_LOAD_LIST_FILES);

		// Change working directory operation
		CHANGE_WORKING_DIRECTORY = new BackgroundOperation();
		CHANGE_WORKING_DIRECTORY.mId = CHANGE_WORKING_DIRECTORY_ID;
		CHANGE_WORKING_DIRECTORY.mBeginEvents.add(FileManagerEvent.WILL_LOAD_LIST_FILES);
		CHANGE_WORKING_DIRECTORY.mEndEvents.add(FileManagerEvent.DID_LOAD_LIST_FILES);

		// Change files order
		CHANGE_ORDER_BY = new BackgroundOperation();
		CHANGE_ORDER_BY.mId = CHANGE_ORDER_BY_ID;
		CHANGE_ORDER_BY.mBeginEvents.add(FileManagerEvent.WILL_LOAD_LIST_FILES);
		CHANGE_ORDER_BY.mEndEvents.add(FileManagerEvent.DID_LOAD_LIST_FILES);

		// Delete list of files
		DELETE_FILES = new BackgroundOperation();
		DELETE_FILES.mId = DELETE_FILES_ID;
		DELETE_FILES.mBeginEvents.add(FileManagerEvent.WILL_LOAD_LIST_FILES);
		DELETE_FILES.mEndEvents.add(FileManagerEvent.DID_LOAD_LIST_FILES);

		// Create new folder
		CREATE_NEW_FOLDER = new BackgroundOperation();
		CREATE_NEW_FOLDER.mId = CREATE_NEW_FOLDER_ID;
		CREATE_NEW_FOLDER.mBeginEvents.add(FileManagerEvent.WILL_LOAD_LIST_FILES);
		CREATE_NEW_FOLDER.mEndEvents.add(FileManagerEvent.DID_LOAD_LIST_FILES);

		// Rename file
		RENAME_FILE = new BackgroundOperation();
		RENAME_FILE.mId = RENAME_FILE_ID;
		RENAME_FILE.mBeginEvents.add(FileManagerEvent.WILL_LOAD_LIST_FILES);
		RENAME_FILE.mEndEvents.add(FileManagerEvent.DID_LOAD_LIST_FILES);

		// Refresh list files
		REFRESH = new BackgroundOperation();
		REFRESH.mId = REFRESH_ID;
		REFRESH.mBeginEvents.add(FileManagerEvent.WILL_LOAD_LIST_FILES);
		REFRESH.mEndEvents.add(FileManagerEvent.DID_LOAD_LIST_FILES);
	}

	private BackgroundOperation() {

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
