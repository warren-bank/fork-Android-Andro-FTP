package net.abachar.androftp.filelist;

/**
 * 
 * @author abachar
 */
public class FileEntry {

	/** File name */
	private String name;

	/** Absolute file path */
	private String path;

	/** File type */
	private FileType type;

	/** File size in bytes */
	private long size;

	/** File last modified date */
	private long lastModified;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the type
	 */
	public FileType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(FileType type) {
		this.type = type;
	}

	/**
	 * @return the size
	 */
	public long getSize() {
		return size;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(long size) {
		this.size = size;
	}

	/**
	 * @return the lastModified
	 */
	public long getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified
	 *            the lastModified to set
	 */
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isDirectory() {
		return type == FileType.FOLDER;
	}
}
