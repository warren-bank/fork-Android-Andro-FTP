package net.abachar.androftp.filelist;

import java.io.File;
import java.util.List;

import net.abachar.androftp.MainActivity;
import net.abachar.androftp.R;
import net.abachar.androftp.filelist.manager.FileEntry;
import net.abachar.androftp.transfers.manager.Transfer;
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
		mWideBrowserFileManager = ((MainActivity) getActivity()).getServerFileManager();
		mSmallBrowserFileManager = ((MainActivity) getActivity()).getLocalFileManager();

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
	 * @see net.abachar.androftp.filelist.AbstractManagerFragment#onMenuTransfer()
	 */
	@Override
	protected void onMenuTransfer() {

		List<FileEntry> selectedFiles = mWideBrowserFileAdapter.getSelectedFiles();
		for (FileEntry file : selectedFiles) {

			Transfer transfer = new Transfer();
			transfer.setNewTransfer(true);
			transfer.setDirection(TransferDirection.DOWNLOAD);
			transfer.setSourcePath(file.getAbsolutePath());
			transfer.setDestinationPath(mSmallBrowserFileManager.getCurrentPath() + File.separator + file.getName());
			transfer.setFileSize(file.getSize());
			transfer.setProgress(0);

			mTransferManager.addTransfer(transfer);
		}

		mActionMode.finish();
	}
}
