package net.abachar.androftp.filelist;

import java.io.File;
import java.util.HashMap;

import org.apache.commons.net.ftp.FTPFile;

import net.abachar.androftp.Application;
import net.abachar.androftp.R;
import android.graphics.drawable.Drawable;

/**
 * 
 * @author abachar
 */
public enum FileType {

	// Documents
	FOLDER(null, R.drawable.ic_file_folder),

	// Character-based files
	TEXT("txt", R.drawable.ic_file_txt),

	// Documents
	DOC("doc", R.drawable.ic_file_doc), PPT("ppt", R.drawable.ic_file_ppt), XLS("xls", R.drawable.ic_file_xls), PDF("pdf", R.drawable.ic_file_pdf),

	// Images
	JPEG("jpg", R.drawable.ic_file_image), GIF("gif", R.drawable.ic_file_image), PNG("png", R.drawable.ic_file_image),

	// Audio
	MP3("mp3", R.drawable.ic_file_mp3),

	// Video
	AVI("avi", R.drawable.ic_file_video), MP4("mp4", R.drawable.ic_file_mp4),

	// Uncknown files
	UNKNOWN(null, R.drawable.ic_file_unknown);

	/** Type extension */
	private final String ext;
	private final Integer iconId;
	
	/** Maps */
	private final static HashMap<Integer, Drawable> icons = new HashMap<Integer, Drawable>();
	private final static HashMap<String, FileType> cache = new HashMap<String, FileType>();

	/**
	 * 
	 * @param ext
	 */
	FileType(String ext, int iconId) {
		this.ext = ext;
		this.iconId = new Integer(iconId);
	}

	/**
	 * @return the ext
	 */
	public String getExtension() {
		return ext;
	}

	/**
	 * 
	 */
	public Drawable getIcon() {
		if (!icons.containsKey(iconId)) {
			icons.put(iconId, Application.getInstance().getResources().getDrawable(iconId.intValue()));
		}
		return icons.get(iconId);
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public static FileType fromFile(File file) {

		if (file.isDirectory()) {
			return FOLDER;
		}
		return fromFileName(file.getName());
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public static FileType fromFTPFile(FTPFile file) {

		if (file.isDirectory()) {
			return FOLDER;
		}
		return fromFileName(file.getName());
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public static FileType fromFileName(String fileName) {

		// Get extension
		int lastDot = fileName.lastIndexOf('.');
		if (lastDot < 0) {
			return UNKNOWN;
		}

		// By extension
		String fext = fileName.substring(lastDot + 1).toLowerCase();
		if (!cache.containsKey(fext)) {
			FileType ftype = UNKNOWN;
			for (FileType type : FileType.values()) {
				if (fext.equals(type.ext)) {
					ftype = type;
					break;
				}
			}
			cache.put(fext, ftype);
		}

		return cache.get(fext);
	}
}