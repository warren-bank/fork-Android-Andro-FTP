package net.abachar.androftp.transfers.manager;

import java.util.List;

import android.os.AsyncTask;

public abstract class TransferTask extends AsyncTask<Transfer, Integer, String> {

	/** */
	protected TransferTaskProgressListener mProgressListener;

	/** */
	protected final List<Transfer> mTransferList;
	protected Transfer mCurrentTransfer;

	/**
	 *
	 */
	public TransferTask(TransferTaskProgressListener progressListener, List<Transfer> transferList) {
		mProgressListener = progressListener;
		mTransferList = transferList;
	}

	/**
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgressListener.onBeginTransferTask(this);
	}

	/**
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected String doInBackground(Transfer... transfers) {

		for (;;) {
			mCurrentTransfer = null;
			synchronized (mTransferList) {
				for (Transfer t : mTransferList) {
					if (t.isPending()) {
						mCurrentTransfer = t;
						mCurrentTransfer.setPending(false);
						break;
					}
				}
			}

			// No pending transfer
			if (mCurrentTransfer == null) {
				break;
			}

			// Prepare chosen transfer
			mCurrentTransfer.setProgress(0);
			publishProgress(mCurrentTransfer.getId(), 0);

			// Download or upload
			if (mCurrentTransfer.getDirection() == TransferDirection.DOWNLOAD) {
				doInBackgroundDownload();
			} else /* if (transfer.getDirection() == TransferDirection.UPLOAD) */{
				doInBackgroundUpload();
			}
		}

		return null;
	}

	/**
	 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
	 */
	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);

		int transferId = values[0];
		int progress = values[1];

		// Publish update
		switch (progress) {
			case 0:
				mProgressListener.onBeginTransfer(this, transferId);
				break;

			case 101: /* Finished */
				mProgressListener.onEndTransfer(this, transferId);
				break;

			default:
				mProgressListener.onProgressUpdate(this, transferId);
		}
	}

	/**
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
	}

	/**
	 * 
	 */
	protected abstract void doInBackgroundDownload();

	/**
	 * 
	 */
	protected abstract void doInBackgroundUpload();
}
