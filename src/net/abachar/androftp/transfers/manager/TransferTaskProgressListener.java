package net.abachar.androftp.transfers.manager;

/**
 * 
 * @author abachar
 */
public interface TransferTaskProgressListener {

	/**
	 *
	 */
	void onBeginTransfer();

	/**
	 * 
	 */
	void onProgressUpdate();

	/**
	 * 
	 */
	void onEndTransfer();
}
