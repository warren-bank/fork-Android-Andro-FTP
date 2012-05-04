package net.abachar.androftp.transfers.manager;

/**
 * 
 * @author abachar
 */
public interface TransferTaskProgressListener {

	/**
	 *
	 */
	void onBeginTransferTask(TransferTask task);

	/**
	 *
	 */
	void onBeginTransfer(TransferTask task, int transferId);

	/**
	 * 
	 */
	void onProgressUpdate(TransferTask task, int transferId);

	/**
	 * 
	 */
	void onEndTransfer(TransferTask task, int transferId);

	/**
	 * 
	 */
	void onEndTransferTask(TransferTask task);
}
