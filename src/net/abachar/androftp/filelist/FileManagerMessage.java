package net.abachar.androftp.filelist;

/**
 * 
 * @author abachar
 */
public enum FileManagerMessage {
	
	/**/
	BEGIN_CONNECT,
	END_CONNECT,
	
	/* */
	INITIAL_LIST_FILES, 

	/* List files changed */
	BEGIN_UPDATE_LIST_FILES,
	END_UPDATE_LIST_FILES;
}
