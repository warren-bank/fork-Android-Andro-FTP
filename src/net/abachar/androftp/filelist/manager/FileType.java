package net.abachar.androftp.filelist.manager;

import java.io.File;
import java.util.HashMap;

import org.apache.commons.net.ftp.FTPFile;

import net.abachar.androftp.MainApplication;
import net.abachar.androftp.R;
import android.graphics.drawable.Drawable;

/**
 * 
 * @author abachar
 */
public enum FileType {

	// Documents
	FOLDER(null, R.drawable.ic_file_folder, R.string.file_type_format_folder),

	// Character-based files
	TEXT("txt", R.drawable.ic_file_txt, R.string.file_type_format_unknown),

	// Documents
	DOC("doc", R.drawable.ic_file_doc, R.string.file_type_format_unknown),
	PPT("ppt", R.drawable.ic_file_ppt, R.string.file_type_format_unknown), 
	XLS("xls", R.drawable.ic_file_xls, R.string.file_type_format_unknown), 
	PDF("pdf", R.drawable.ic_file_pdf, R.string.file_type_format_unknown),

	// Images
	JPEG("jpg", R.drawable.ic_file_image, R.string.file_type_format_unknown), 
	GIF("gif", R.drawable.ic_file_image, R.string.file_type_format_unknown), 
	PNG("png", R.drawable.ic_file_image, R.string.file_type_format_unknown),

	// Audio
	MP3("mp3", R.drawable.ic_file_mp3, R.string.file_type_format_unknown),

	// Video
	AVI("avi", R.drawable.ic_file_video, R.string.file_type_format_unknown), 
	MP4("mp4", R.drawable.ic_file_mp4, R.string.file_type_format_unknown),

	// Uncknown files
	UNKNOWN(null, R.drawable.ic_file_unknown, R.string.file_type_format_unknown);

	/** Type extension */
	private final String ext;
	private final Integer iconId;
	private final int formatId;

	/** Maps */
	private final static HashMap<Integer, Drawable> icons = new HashMap<Integer, Drawable>();
	private final static HashMap<String, FileType> cache = new HashMap<String, FileType>();

	/**
	 * 
	 * @param ext
	 */
	FileType(String ext, int iconId, int formatId) {
		this.ext = ext;
		this.iconId = new Integer(iconId);
		this.formatId = formatId;
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
			icons.put(iconId, MainApplication.getInstance().getResources().getDrawable(iconId.intValue()));
		}
		return icons.get(iconId);
	}

	/**
	 * @return the formatId
	 */
	public String getFormat() {
		return MainApplication.getInstance().getString(formatId);
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