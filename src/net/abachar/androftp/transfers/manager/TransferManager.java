package net.abachar.androftp.transfers.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.abachar.androftp.filelist.manager.FileManagerListener;
import net.abachar.androftp.filelist.manager.OrderBy;
import net.abachar.androftp.filelist.manager.OrderByComparator;

import android.content.Context;
import android.os.AsyncTask;

/**
 * 
 * @author abachar
 */
public class TransferManager {

	/** Context */
	private Context mContext;

	/** List of all active transfers */
	private List<Transfer> mTransferList;

	/**
	 * Default constructor
	 */
	public TransferManager(Context context) {
		mContext = context;
		mTransferList = new ArrayList<Transfer>();
	}

	/**
	 * 
	 * @param transfer
	 */
	public void addTransfer(Transfer transfer) {
		mTransferList.add(transfer);
		new BackgroundTransferTask(transfer).execute();
	}

	/**
	 * Do work on second thread class
	 */
	private class BackgroundTransferTask extends AsyncTask<Void, Integer, String> {

		/** */
		private Transfer transfer;

		/** */
		private BackgroundTransferTask(Transfer transfer) {
			this.transfer = transfer;
		}

		/**
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			// // Send begin events
			// notifyListeners(mOperation.getBeginEvents());
		}

		/**
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected String doInBackground(Void... params) {
			// BackgroundOperationResult result = new
			// BackgroundOperationResult();
			//
			// try {
			// switch (mOperation.getId()) {
			//
			// case BackgroundOperation.CONNECT_ID:
			// doConnect();
			// break;
			//
			// case BackgroundOperation.CHANGE_TO_PARENT_DIRECTORY_ID:
			// doChangeToParentDirectory();
			// break;
			//
			// case BackgroundOperation.CHANGE_WORKING_DIRECTORY_ID:
			// doChangeWorkingDirectory(params[0]);
			// break;
			//
			// case BackgroundOperation.CHANGE_ORDER_BY_ID:
			// Collections.sort(mFileList, mOrderByComparator);
			// break;
			//
			// case BackgroundOperation.DELETE_FILES_ID:
			// doDeleteFiles(params);
			// break;
			//
			// case BackgroundOperation.CREATE_NEW_FOLDER_ID:
			// doCreateNewfolder(params[0]);
			// break;
			//
			// case BackgroundOperation.RENAME_FILE_ID:
			// doRenameFile(params[0], params[1]);
			// break;
			//
			// case BackgroundOperation.REFRESH_ID:
			// doRefresh();
			// break;
			// }
			//
			// result.setSuccess(true);
			// } catch (ConnectionException ex) {
			// result.setSuccess(false);
			// result.addReplacementEvent(FileManagerEvent.ERROR_CONNECTION);
			//
			// } catch (FileManagerException ex) {
			// result.setSuccess(false);
			// }
			return null;
		}

		/**
		 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		/**
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		}
	}
}