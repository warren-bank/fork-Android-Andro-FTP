package net.abachar.androftp.transfers;

/**
 * 
 * @author abachar
 */
public class Transfer {

	/** */
	private boolean checked;

	/** */
	private TransferDirection direction;

	/** */
	private String sourcePath;

	/** */
	private String destinationPath;

	/** */
	private String stringFileSize;

	/** */
	private int progress;

	/**
	 * Default constructor
	 */
	public Transfer() {
		this.checked = false;
		this.direction = TransferDirection.UPLOAD;
		this.sourcePath = "";
		this.destinationPath = "";
		this.stringFileSize = "";
		this.progress = 0;
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
	 * @return the stringFileSize
	 */
	public String getStringFileSize() {
		return stringFileSize;
	}

	/**
	 * @param stringFileSize
	 *            the stringFileSize to set
	 */
	public void setStringFileSize(String stringFileSize) {
		this.stringFileSize = stringFileSize;
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
