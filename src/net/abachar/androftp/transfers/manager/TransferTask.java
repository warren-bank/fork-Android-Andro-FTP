package net.abachar.androftp.transfers.manager;

import android.os.AsyncTask;

public abstract class TransferTask extends AsyncTask<Transfer, Integer, String> {

	/**
	 * 
	 */
	protected TransferTaskProgressListener mProgressListener;

	/**
	 * 
	 */
	protected Transfer transfer;

	/**
	 *
	 */
	public TransferTask(TransferTaskProgressListener progressListener) {
		mProgressListener = progressListener;
	}

	/**
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	/**
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected String doInBackground(Transfer... transfers) {

		// Get transfer
		transfer = transfers[0];
		transfer.setProgress(0);

		//
		if (transfer.getDirection() == TransferDirection.DOWNLOAD) {
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
		mProgressListener.onProgressUpdate();
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
