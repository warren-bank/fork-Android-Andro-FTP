package net.abachar.androftp.transfers.manager;

import java.io.File;

/**
 * 
 * @author abachar
 */
public class Transfer {

	/** Transfer UID */
	private int id;

	/** */
	private boolean checked;

	/** Awaiting download */
	private boolean pending;

	/** Transfer direction */
	private TransferDirection direction;

	/** File name */
	private String name;

	/** Source directory */
	private String sourcePath;

	/** Destination directory */
	private String destinationPath;

	/** */
	private long fileSize;

	/** */
	private int progress;

	/**
	 * 
	 */
	public Transfer(int id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the checked
	 */
	public boolean isChecked() {
		return checked;
	}

	/**
	 * @param checked
	 *            the checked to set
	 */
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	/**
	 * @return the pending
	 */
	public boolean isPending() {
		return pending;
	}

	/**
	 * @param pending
	 *            the pending to set
	 */
	public void setPending(boolean pending) {
		this.pending = pending;
	}

	/**
	 * @return the direction
	 */
	public TransferDirection getDirection() {
		return direction;
	}

	/**
	 * @param direction
	 *            the direction to set
	 */
	public void setDirection(TransferDirection direction) {
		this.direction = direction;
	}

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
	 * @return the sourcePath
	 */
	public String getSourcePath() {
		return sourcePath;
	}

	/**
	 * @param sourcePath
	 *            the sourcePath to set
	 */
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	/**
	 * @return the destinationPath
	 */
	public String getDestinationPath() {
		return destinationPath;
	}

	/**
	 * @param destinationPath
	 *            the destinationPath to set
	 */
	public void setDestinationPath(String destinationPath) {
		this.destinationPath = destinationPath;
	}

	/**
	 * @return the fileSize
	 */
	public long getFileSize() {
		return fileSize;
	}

	/**
	 * @param fileSize
	 *            the fileSize to set
	 */
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	/**
	 * @return the progress
	 */
	public int getProgress() {
		return progress;
	}

	/**
	 * @param progress
	 *            the progress to set
	 */
	public void setProgress(int progress) {
		this.progress = progress;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isUpload() {
		return direction == TransferDirection.UPLOAD;
	}

	/**
	 * 
	 */
	public String toStringSourcePath() {

		if (direction == TransferDirection.UPLOAD) {
			return "Local:" + getFullSourcePath();
		} /* else { */
		return "FTP:" + getFullSourcePath();
		/* } */
	}

	/**
	 * 
	 */
	public String getFullSourcePath() {
		StringBuilder sb = new StringBuilder();

		// Path
		sb.append(sourcePath);
		if (!sourcePath.endsWith(File.separator)) {
			sb.append(File.separator);
		}

		// File name
		sb.append(name);

		return sb.toString();
	}

	/**
	 * 
	 */
	public String toStringDestinationPath() {

		if (direction == TransferDirection.DOWNLOAD) {
			return "Local:" + getFullDestinationPath();
		} /* else { */
		return "FTP:" + getFullDestinationPath();
		/* } */
	}

	/**
	 * 
	 */
	public String getFullDestinationPath() {
		StringBuilder sb = new StringBuilder();

		// Path
		sb.append(destinationPath);
		if (!destinationPath.endsWith(File.separator)) {
			sb.append(File.separator);
		}

		// File name
		sb.append(name);

		return sb.toString();
	}
}
