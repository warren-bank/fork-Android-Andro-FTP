package net.abachar.androftp.transfers.manager;

import android.os.AsyncTask;

public abstract class TransferTask extends AsyncTask<Transfer, Integer, String> {

	/** */
	protected TransferTaskProgressListener mProgressListener;

	/** */
	protected Transfer mTransfer;

	/** */
	protected boolean mComplete;

	/**
	 *
	 */
	public TransferTask(TransferTaskProgressListener progressListener) {
		mProgressListener = progressListener;
		mComplete = false;
	}

	/**
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected String doInBackground(Transfer... transfers) {

		// Get transfer
		mTransfer = transfers[0];
		mTransfer.setProgress(0);

		// Start transfer
		publishProgress(0);

		// Download or upload
		if (mTransfer.getDirection() == TransferDirection.DOWNLOAD) {
			doInBackgroundDownload();
		} else /* if (transfer.getDirection() == TransferDirection.UPLOAD) */{
			doInBackgroundUpload();
		}

		return null;
	}

	/**
	 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
	 */
	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);

		int p = values[0].intValue();
		mTransfer.setProgress(p);

		// Publish update
		if (p == 0) {
			mProgressListener.onBeginTransfer();
		} else {
			mProgressListener.onProgressUpdate();
		}
	}

	/**
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);

		// Full tranfer
		mTransfer.setProgress(100);
		mComplete = true;
		mProgressListener.onEndTransfer(mTransfer.getId());
	}

	/**
	 * @return the mComplete
	 */
	public boolean isComplete() {
		return mComplete;
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
