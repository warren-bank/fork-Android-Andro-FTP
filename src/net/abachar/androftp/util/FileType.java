package net.abachar.androftp.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.abachar.androftp.MainApplication;
import net.abachar.androftp.R;

import org.apache.commons.net.ftp.FTPFile;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

/**
 * 
 * @author abachar
 */
public class FileType {

	// Folder and unknow file
	public final static FileType FOLDER = new FileType("FOLDER", 0, null, R.drawable.ic_file_folder, R.string.file_type_format_folder, false);
	public final static FileType UNKNOWN = new FileType("UNKNOWN", 3000, null, R.drawable.ic_file_unknown, R.string.file_type_format_unknown, false);

	/** Type extension */
	private String mCode;
	private String mExtension;
	private int mOrder;
	private Drawable mIcon;
	private String mFormat;
	private boolean mAscii;

	/** Maps */
	private final static Map<String, FileType> fileTypes = new HashMap<String, FileType>();

	/**
	 * 
	 */
	private FileType(String code, int order, String extension, int iconId, int formatId, boolean ascii) {

		mCode = code;
		mOrder = order;
		mExtension = extension;
		mAscii = ascii;
		mIcon = MainApplication.getInstance().getResources().getDrawable(iconId);
		mFormat = MainApplication.getInstance().getString(formatId);
	}

	/**
	 * 
	 * @param code
	 * @param extension
	 * @param icon
	 * @param format
	 * @param ascii
	 */
	public static void addFileType(String code, int order, String extension, String icon, String format, String ascii) {

		Resources resources = MainApplication.getInstance().getResources();

		// Load icon Id
		int iconId = R.drawable.ic_file_unknown;
		if (icon != null) {
			iconId = resources.getIdentifier(icon, "drawable", "net.abachar.androftp");
		}

		// Load format id
		int formatId = R.string.file_type_format_unknown;
		if (format != null) {
			formatId = resources.getIdentifier(format, "string", "net.abachar.androftp");
		}

		// String icon, String format, String ascii
		boolean isAscii = false;
		if (ascii != null) {
			isAscii = Boolean.parseBoolean(ascii);
		}

		// Add new file type
		fileTypes.put(extension, new FileType(code, order, extension, iconId, formatId, isAscii));
	}

	/**
	 * @return the mCode
	 */
	public String getCode() {
		return mCode;
	}

	/**
	 * @return the ext
	 */
	public String getExtension() {
		return mExtension;
	}

	/**
	 * @return the order
	 */
	public int getOrder() {
		return mOrder;
	}

	/**
	 * 
	 */
	public Drawable getIcon() {
		return mIcon;
	}

	/**
	 * @return the formatId
	 */
	public String getFormat() {
		return mFormat;
	}

	/**
	 * @return the mAscii
	 */
	public boolean isAscii() {
		return mAscii;
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
		String ext = fileName.substring(lastDot + 1).toLowerCase();
		return fileTypes.containsKey(ext) ? fileTypes.get(ext) : UNKNOWN;
	}
}