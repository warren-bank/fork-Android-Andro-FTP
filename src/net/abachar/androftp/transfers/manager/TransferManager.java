package net.abachar.androftp.transfers.manager;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;

import net.abachar.androftp.MainApplication;
import net.abachar.androftp.filelist.manager.FileEntry;
import net.abachar.androftp.util.Constants;

/**
 * 
 * @author abachar
 */
public class TransferManager implements TransferTaskProgressListener {

	/** */
	private TransferListener listener;

	/** List of all active transfers */
	private int mTransferSequense;
	private final List<Transfer> mTransferList;

	/** */
	private int mMaxTransferTaskCount;
	private final List<TransferTask> mTransferTasks;

	/**
	 * Default constructor
	 */
	public TransferManager() {
		mTransferSequense = 0;
		mTransferList = new ArrayList<Transfer>();
		mTransferTasks = new ArrayList<TransferTask>();
	}

	/**
	 *
	 */
	public void init(Bundle bundle) {
		mMaxTransferTaskCount = bundle.getInt(Constants.PREFERENCE_MAX_SIMULTANEOUS_TRANSFERS);
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
		synchronized (mTransferList) {
			mTransferList.add(transfer);
		}
	}

	/**
	 * 
	 */
	public void processTransferQueue() {
		synchronized (mTransferList) {

			// No task ?
			if ((mTransferList == null) || mTransferList.isEmpty()) {
				return;
			}

			// While we have available task and pending transfer
			for (;;) {
				if (mTransferTasks.size() < mMaxTransferTaskCount) {

					// Pending transfer
					Transfer pendingTransfer = null;
					for (Transfer transfer : mTransferList) {
						if (transfer.isPending()) {
							pendingTransfer = transfer;
							break;
						}
					}

					// No pending transfer
					if (pendingTransfer == null) {
						break;
					}

					// Create new transfer task
					FTPTransferTask task = new FTPTransferTask(this, mTransferList);
					mTransferTasks.add(task);
					task.execute();
				} else {
					break;
				}
			}
		}
	}

	/**
	 * @see net.abachar.androftp.transfers.manager.TransferTaskProgressListener#onBeginTransferTask(net.abachar.androftp.transfers.manager.TransferTask)
	 */
	@Override
	public void onBeginTransferTask(TransferTask task) {
	}

	/**
	 * @see net.abachar.androftp.transfers.manager.TransferTaskProgressListener#onBeginTransfer(net.abachar.androftp.transfers.manager.TransferTask,
	 *      int)
	 */
	@Override
	public void onBeginTransfer(TransferTask task, int transferId) {

		if (listener != null) {
			listener.onUpdateTransfer();
		}
	}

	/**
	 * @see net.abachar.androftp.transfers.manager.TransferTaskProgressListener#onProgressUpdate(net.abachar.androftp.transfers.manager.TransferTask,
	 *      int)
	 */
	@Override
	public void onProgressUpdate(TransferTask task, int transferId) {

		if (listener != null) {
			listener.onUpdateTransfer();
		}
	}

	/**
	 * @see net.abachar.androftp.transfers.manager.TransferTaskProgressListener#onEndTransfer(net.abachar.androftp.transfers.manager.TransferTask,
	 *      int)
	 */
	@Override
	public void onEndTransfer(TransferTask task, int transferId) {
		synchronized (mTransferList) {

			// Find complete transfer
			Transfer completeTransfer = null;
			for (Transfer transfer : mTransferList) {
				if (transfer.getId() != transferId) {
					completeTransfer = transfer;
					break;
				}
			}

			// Remove it
			mTransferList.remove(completeTransfer);
		}

		// Notify listner
		if (listener != null) {
			listener.onUpdateTransfer();
		}
	}

	/**
	 * @see net.abachar.androftp.transfers.manager.TransferTaskProgressListener#onEndTransferTask(net.abachar.androftp.transfers.manager.TransferTask)
	 */
	@Override
	public void onEndTransferTask(TransferTask task) {

		// Remove finiched task
		mTransferTasks.remove(task);
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

	/**
	 * @param mMaxTransferTaskCount
	 *            the mMaxTransferTaskCount to set
	 */
	public void setMaxTransferTaskCount(int mMaxTransferTaskCount) {
		this.mMaxTransferTaskCount = mMaxTransferTaskCount;
	}
}