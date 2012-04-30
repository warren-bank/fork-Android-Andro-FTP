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
public class ServerManagerFragment extends AbstractManagerFragment {

	/**
	 * Default constructor.
	 */
	public ServerManagerFragment() {
		super();

		// Local layout
		mFragmentLayoutId = R.layout.fragment_server_browsers;
	}

	/**
	 * Setup fragment view
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// Init file managers
		mWideBrowserFileManager = MainApplication.getInstance().getServerFileManager();
		mSmallBrowserFileManager = MainApplication.getInstance().getLocalFileManager();

		// Create view
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	/**
	 * @see net.abachar.androftp.filelist.AbstractManagerFragment#onCreateActionMode(android.view.Menu)
	 */
	@Override
	protected boolean onCreateActionMode(Menu menu) {

		// Menu download
		mDownloadMenu = menu.add(Menu.NONE, 1, Menu.NONE, R.string.menu_download);
		mDownloadMenu.setIcon(R.drawable.ic_action_download);
		mDownloadMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

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
			// mDownloadMenu.setIcon(R.drawable.ic_action_download_off);
			mDownloadMenu.setEnabled(false);
		} else {
			mDownloadMenu.setIcon(R.drawable.ic_action_download);
			mDownloadMenu.setEnabled(true);
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
			mTransferManager.addToTransferQueue(TransferDirection.DOWNLOAD, file);
		}

		// process queue
		mTransferManager.processTransferQueue();

		mActionMode.finish();
	}
}
