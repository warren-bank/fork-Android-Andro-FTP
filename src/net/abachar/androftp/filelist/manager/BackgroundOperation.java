package net.abachar.androftp.filelist.manager;

/**
 * 
 */
public enum BackgroundOperation {
	CONNECT,
	
	CHANGE_TO_PARENT_DIRECTORY,
	CHANGE_WORKING_DIRECTORY,
	CHANGE_ORDER_BY,
	
	DELETE_FILES,
	CREATE_NEW_FOLDER,
	RENAME_FILE,
	REFRESH;
}
