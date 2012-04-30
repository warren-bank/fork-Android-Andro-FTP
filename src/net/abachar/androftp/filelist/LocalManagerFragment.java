package net.abachar.androftp.filelist;

import java.util.List;

import net.abachar.androftp.MainApplication;
import net.abachar.androftp.R;
import net.abachar.androftp.filelist.manager.FileEntry;
import net.abachar.androftp.transfers.manager.TransferDirection;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * @author abachar
 */
public class LocalManagerFragment extends AbstractManagerFragment {

	/**
	 * Default constructor.
	 */
	public LocalManagerFragment() {
		super();

		// Local layout
		mFragmentLayoutId = R.layout.fragment_local_browsers;
	}

	/**
	 * Setup fragment view
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// Init file managers
		mWideBrowserFileManager = MainApplication.getInstance().getLocalFileManager();
		mSmallBrowserFileManager = MainApplication.getInstance().getServerFileManager();

		// Create view
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	/**
	 * @see net.abachar.androftp.filelist.AbstractManagerFragment#onCreateActionMode(android.view.Menu)
	 */
	@Override
	protected boolean onCreateActionMode(Menu menu) {

		// Menu upload
		mUploadMenu = menu.add(Menu.NONE, ID_MENU_UPLOAD, Menu.NONE, R.string.menu_upload);
		mUploadMenu.setIcon(R.drawable.ic_action_upload);
		mUploadMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		return super.onCreateActionMode(menu);
	}

	/**
	 * 
	 * @param menu
	 * @return
	 */
	protected boolean onPrepareActionMode(Menu menu) {

		// Enable/disable upload/download actions
		boolean isFolderSelected = false;
		List<FileEntry> files = mWideBrowserFileAdapter.getSelectedFiles();
		if ((files != null) && !files.isEmpty()) {
			for (FileEntry file : files) {
				if (file.isFolder()) {
					isFolderSelected = true;
					break;
				}
			}
		}

		if (isFolderSelected) {
			// mUploadMenu.setIcon(R.drawable.ic_action_upload_off);
			mUploadMenu.setEnabled(false);
		} else {
			mUploadMenu.setIcon(R.drawable.ic_action_upload);
			mUploadMenu.setEnabled(true);
		}

		return super.onPrepareActionMode(menu);
	}

	/**
	 * @see net.abachar.androftp.filelist.AbstractManagerFragment#onMenuTransfer()
	 */
	@Override
	protected void onMenuTransfer() {

		// Add files to queue
		List<FileEntry> selectedFiles = mWideBrowserFileAdapter.getSelectedFiles();
		for (FileEntry file : selectedFiles) {
			mTransferManager.addToTransferQueue(TransferDirection.UPLOAD, file);
		}
		
		// process queue
		mTransferManager.processTransferQueue();

		mActionMode.finish();
	}
}
