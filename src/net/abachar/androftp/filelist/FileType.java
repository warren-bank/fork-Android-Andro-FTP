package net.abachar.androftp.filelist;

/**
 * 
 * @author abachar
 */
public enum FileType {

	// Documents
	FOLDER(null),

	// Character-based files
	TEXT("txt"),

	// Documents
	DOC("doc"), PPT("ppt"), XLS("xls"), PDF("pdf"),

	// Images
	JPEG("jpg"), GIF("gif"), PNG("png"),

	// Audio
	MP3("mp3"),

	// Video
	AVI("avi"), MP4("mp4"),

	// Uncknown files
	UNKNOWN(null);

	/** Type extension */
	private final String ext;

	/**
	 * 
	 * @param ext
	 */
	FileType(String ext) {
		this.ext = ext;
	}

	/**
	 * @return the ext
	 */
	public String getExtension() {
		return ext;
	}
}