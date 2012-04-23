package net.abachar.androftp.filelist;

import net.abachar.androftp.MainActivity;
import net.abachar.androftp.R;
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

	/** */
	private MenuItem mDownloadMenu;
	private MenuItem mDeleteMenu;
	private MenuItem mRenameMenu;
	private MenuItem mDetailMenu;

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
	 * 
	 */
	protected boolean onCreateActionMode(Menu menu) {

		// Menu download
		mDownloadMenu = menu.add(Menu.NONE, 1, Menu.NONE, R.string.menu_download);
		mDownloadMenu.setIcon(R.drawable.ic_action_download);
		mDownloadMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		// Menu delete
		mDeleteMenu = menu.add(Menu.NONE, 2, Menu.NONE, R.string.menu_delete);
		mDeleteMenu.setIcon(R.drawable.ic_action_delete);
		mDeleteMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		// Menu rename
		mRenameMenu = menu.add(Menu.NONE, 1, Menu.NONE, R.string.menu_rename);
		mRenameMenu.setIcon(R.drawable.ic_action_rename);
		mRenameMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		// Menu detail
		mDetailMenu = menu.add(Menu.NONE, 1, Menu.NONE, R.string.menu_detail);
		mDetailMenu.setIcon(R.drawable.ic_action_detail);
		mDetailMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		return true;
	}

	/**
	 * 
	 */
	protected boolean onPrepareActionMode(Menu menu) {

		if (mMultiSelect) {
			if (mRenameMenu.isEnabled()) {
				mRenameMenu.setIcon(R.drawable.ic_action_rename_off);
				mDetailMenu.setIcon(R.drawable.ic_action_detail_off);
				mRenameMenu.setEnabled(false);
				mDetailMenu.setEnabled(false);
				return true;
			}
		} else {
			if (!mRenameMenu.isEnabled()) {
				mRenameMenu.setIcon(R.drawable.ic_action_rename);
				mDetailMenu.setIcon(R.drawable.ic_action_detail);
				mRenameMenu.setEnabled(true);
				mDetailMenu.setEnabled(true);
				return true;
			}
		}

		return false;
	}

	/**
	 * 
	 */
	protected boolean onActionItemClicked(MenuItem item) {
		return false;
	}
}
