package net.abachar.androftp.transfers.manager;

import java.util.ArrayList;
import java.util.List;

import net.abachar.androftp.MainApplication;
import net.abachar.androftp.filelist.manager.FileEntry;
import android.content.Context;

/**
 * 
 * @author abachar
 */
public class TransferManager implements TransferTaskProgressListener {

	/** Context */
	// private Context mContext;

	/** */
	private TransferListener listener;

	/** List of all active transfers */
	private int mTransferSequense;
	private List<Transfer> mTransferList;

	/**
	 * Default constructor
	 */
	public TransferManager(Context context) {
		// mContext = context;
		mTransferSequense = 0;
		mTransferList = new ArrayList<Transfer>();
	}

	/**
	 * 
	 * @param direction
	 * @param entry
	 */
	public void addToTransferQueue(TransferDirection direction, FileEntry entry) {

		Transfer transfer = new Transfer(++mTransferSequense);
		transfer.setPending(true);
		transfer.setName(entry.getName());
		transfer.setDirection(direction);
		transfer.setFileSize(entry.getSize());

		// Source path
		transfer.setSourcePath(entry.getParentPath());

		// Destination path
		if (transfer.isUpload()) {
			transfer.setDestinationPath(MainApplication.getInstance().getServerFileManager().getCurrentPath());
		} else {
			transfer.setDestinationPath(MainApplication.getInstance().getLocalFileManager().getCurrentPath());
		}

		// Add transfer
		mTransferList.add(transfer);
	}

	/**
	 * 
	 */
	public void processTransferQueue() {

		if ((mTransferList != null) && !mTransferList.isEmpty()) {
			for (Transfer transfer : mTransferList) {
				if (transfer.isPending()) {

					// Start transfer
					TransferTask task = new FTPTransferTask(this);
					transfer.setPending(false);
					task.execute(transfer);

					// Notify
					if (listener != null) {
						listener.onUpdateTransfer();
					}

					break;
				}
			}
		}
	}

	/**
	 * @see net.abachar.androftp.transfers.manager.TransferTaskProgressListener#onBeginTransfer()
	 */
	@Override
	public void onBeginTransfer() {
	}

	/**
	 * @see net.abachar.androftp.transfers.manager.TransferTaskProgressListener#onProgressUpdate()
	 */
	@Override
	public void onProgressUpdate() {
		if (listener != null) {
			listener.onUpdateTransfer();
		}
	}

	/**
	 * @see net.abachar.androftp.transfers.manager.TransferTaskProgressListener#onEndTransfer()
	 */
	@Override
	public void onEndTransfer() {
	}

	/**
	 * @return the mTransferList
	 */
	public List<Transfer> getTransferList() {
		return mTransferList;
	}

	/**
	 * 
	 * @param listener
	 */
	public void setListener(TransferListener listener) {
		this.listener = listener;

		// Initial
		if ((mTransferList != null) && !mTransferList.isEmpty()) {
			listener.onUpdateTransfer();
		}
	}
}