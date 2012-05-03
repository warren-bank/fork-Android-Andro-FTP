package net.abachar.androftp.transfers.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.abachar.androftp.MainApplication;
import net.abachar.androftp.filelist.manager.FileEntry;

/**
 * 
 * @author abachar
 */
public class TransferManager implements TransferTaskProgressListener {

	/** */
	private TransferListener listener;

	/** List of all active transfers */
	private int mTransferSequense;
	private List<Transfer> mTransferList;

	/** */
	private final int mMaxTransferTaskCount = 2;
	private TransferTask[] mTransferTasks;

	/**
	 * Default constructor
	 */
	public TransferManager() {
		mTransferSequense = 0;
		mTransferList = (List<Transfer>) Collections.synchronizedList(new ArrayList<Transfer>());
		mTransferTasks = new TransferTask[mMaxTransferTaskCount];
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

		// No task ?
		if ((mTransferList == null) || mTransferList.isEmpty()) {
			return;
		}

		// While we have available task and pending transfer
		while (true) {

			// Available task ?
			TransferTask task = null;
			for (int i = 0; i < mMaxTransferTaskCount; i++) {
				if ((mTransferTasks[i] == null) || mTransferTasks[i].isComplete()) {
					task = new FTPTransferTask(this);
					mTransferTasks[i] = task;
					break;
				}
			}

			// No available task
			if (task == null) {
				break;
			}

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

			// Start transfer
			pendingTransfer.setPending(false);
			task.execute(pendingTransfer);
		}
	}

	/**
	 * @see net.abachar.androftp.transfers.manager.TransferTaskProgressListener#onBeginTransfer()
	 */
	@Override
	public void onBeginTransfer() {
		if (listener != null) {
			listener.onUpdateTransfer();
		}
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
	public void onEndTransfer(int transferId) {

		// Delete finished transfer
		List<Transfer> newTransferList = new ArrayList<Transfer>();
		for (Transfer transfer : mTransferList) {
			if (transfer.getId() != transferId) {
				newTransferList.add(transfer);
			}
		}
		mTransferList = newTransferList;

		// Notify listner
		if (listener != null) {
			listener.onUpdateTransfer();
		}

		// Proccess next download
		processTransferQueue();
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