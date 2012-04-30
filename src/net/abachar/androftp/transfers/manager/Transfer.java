package net.abachar.androftp.transfers.manager;

/**
 * 
 * @author abachar
 */
public class Transfer {

	/** */
	private boolean checked;

	/** */
	private boolean newTransfer;

	/** */
	private TransferDirection direction;

	/** */
	private String name;

	/** */
	private String sourcePath;
	private String sourceAbsolutePath;

	/** */
	private String destinationPath;
	private String destinationAbsolutePath;

	/** */
	private long fileSize;

	/** */
	private int progress;

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
	 * @return the newTransfer
	 */
	public boolean isNewTransfer() {
		return newTransfer;
	}

	/**
	 * @param newTransfer
	 *            the newTransfer to set
	 */
	public void setNewTransfer(boolean newTransfer) {
		this.newTransfer = newTransfer;
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
	 * @return the sourceAbsolutePath
	 */
	public String getSourceAbsolutePath() {
		return sourceAbsolutePath;
	}

	/**
	 * @param sourceAbsolutePath
	 *            the sourceAbsolutePath to set
	 */
	public void setSourceAbsolutePath(String sourceAbsolutePath) {
		this.sourceAbsolutePath = sourceAbsolutePath;
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
	 * @return the destinationAbsolutePath
	 */
	public String getDestinationAbsolutePath() {
		return destinationAbsolutePath;
	}

	/**
	 * @param destinationAbsolutePath
	 *            the destinationAbsolutePath to set
	 */
	public void setDestinationAbsolutePath(String destinationAbsolutePath) {
		this.destinationAbsolutePath = destinationAbsolutePath;
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
}
