package net.abachar.androftp.transfers.manager;

import java.io.File;
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
	private List<Transfer> mTransferList;

	/**
	 * Default constructor
	 */
	public TransferManager(Context context) {
		// mContext = context;
		mTransferList = new ArrayList<Transfer>();
	}

	/**
	 * 
	 * @param direction
	 * @param entry
	 */
	public void addToTransferQueue(TransferDirection direction, FileEntry entry) {

		Transfer transfer = new Transfer();
		transfer.setNewTransfer(true);
		transfer.setName(entry.getName());
		transfer.setDirection(direction);
		transfer.setFileSize(entry.getSize());
		transfer.setProgress(0);

		// Source path
		transfer.setSourcePath(entry.getParentPath());
		transfer.setSourceAbsolutePath(entry.getAbsolutePath());

		// Destination path
		String destinationPath;
		if (transfer.isUpload()) {
			destinationPath = MainApplication.getInstance().getServerFileManager().getCurrentPath();
		} else {
			destinationPath = MainApplication.getInstance().getLocalFileManager().getCurrentPath();
		}
		transfer.setDestinationPath(destinationPath);

		if (!destinationPath.endsWith(File.separator)) {
			destinationPath = destinationPath + File.separator;
		}
		transfer.setDestinationAbsolutePath(destinationPath + entry.getName());

		// Add transfer
		mTransferList.add(transfer);
	}

	/**
	 * 
	 */
	public void processTransferQueue() {

		if ((mTransferList != null) && !mTransferList.isEmpty()) {
			for (Transfer transfer : mTransferList) {
				if (transfer.isNewTransfer()) {

					// Start transfer
					TransferTask task = new FTPTransferTask(this);
					transfer.setNewTransfer(false);
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
	 * @see net.abachar.androftp.transfers.manager.TransferTaskProgressListener#onProgressUpdate()
	 */
	@Override
	public void onProgressUpdate() {
		if (listener != null) {
			listener.onUpdateTransfer();
		}
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