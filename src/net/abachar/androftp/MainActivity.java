package net.abachar.androftp;

import net.abachar.androftp.filelist.LocalManagerFragment;
import net.abachar.androftp.filelist.ServerManagerFragment;
import net.abachar.androftp.filelist.manager.FileManagerEvent;
import net.abachar.androftp.filelist.manager.FileManagerListener;
import net.abachar.androftp.servers.Logontype;
import net.abachar.androftp.transfers.TransferFragment;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * 
 * @author abachar
 */
public class MainActivity extends Activity implements ActionBar.TabListener, FileManagerListener {

	/** Tab indexs and selected tab index */
	private TabId mSelectedTab;

	/** Connexion progress dialog */
	private ProgressDialog mProgressDialog;

	/** Menus */
	protected MenuItem mSettingMenu;

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Show waiting dialog
		mProgressDialog = ProgressDialog.show(this, getString(R.string.connect_progress_title), getString(R.string.connect_progress_message), true, false);

		// Create map properties
		final Bundle bundle = new Bundle();
		if (savedInstanceState != null) {

		} else {

			// Server data
			Bundle intentExtras = getIntent().getExtras();
			bundle.putString("server.host", intentExtras.getString("host"));
			bundle.putInt("server.port", intentExtras.getInt("port"));
			Logontype logontype = (Logontype) intentExtras.get("logontype");
			bundle.putSerializable("server.logontype", logontype);
			if (logontype == Logontype.NORMAL) {
				bundle.putString("server.username", intentExtras.getString("username"));
				bundle.putString("server.password", intentExtras.getString("password"));
			}

			// Setup selected tab
			mSelectedTab = TabId.LOCAL_MANAGER;
		}

		// Settings
		bundle.putInt("server.timeout", 20 * 1000);

		// Instanciate managers
		MainApplication.getInstance().initManagers(this, bundle);

		// Use main view
		setContentView(R.layout.main);

		// Setup actionbar
		setupActionBar();

		// Connect file managers
		MainApplication.getInstance().connect();
	}

	/**
	 * 
	 */
	private void setupActionBar() {

		ActionBar actionBar = getActionBar();
		// actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM |
		// ActionBar.DISPLAY_USE_LOGO);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);

		// Create local tab
		for (TabId tabId : TabId.values()) {
			ActionBar.Tab tab = actionBar.newTab();
			tab.setText(getString(tabId.textId));
			tab.setTag(new TabTag(tabId));
			tab.setTabListener(this);

			actionBar.addTab(tab);
		}

		// Set selected tab
		actionBar.setSelectedNavigationItem(mSelectedTab.ordinal());
	}

	/**
	 * @see android.app.ActionBar.TabListener#onTabSelected(android.app.ActionBar.Tab,
	 *      android.app.FragmentTransaction)
	 */
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		TabTag tag = (TabTag) tab.getTag();
		Fragment fragment = getFragmentManager().findFragmentByTag(tag.key);
		if (fragment == null) {
			fragment = Fragment.instantiate(this, tag.className);
			ft.add(android.R.id.content, fragment, tag.key);
		} else {
			ft.show(fragment);
		}

		// Set selected tab
		mSelectedTab = tag.tabId;
	}

	/**
	 * @see android.app.ActionBar.TabListener#onTabUnselected(android.app.ActionBar.Tab,
	 *      android.app.FragmentTransaction)
	 */
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		TabTag tag = (TabTag) tab.getTag();
		Fragment fragment = getFragmentManager().findFragmentByTag(tag.key);
		if (fragment != null) {
			ft.hide(fragment);
		}
	}

	/**
	 * @see android.app.ActionBar.TabListener#onTabReselected(android.app.ActionBar.Tab,
	 *      android.app.FragmentTransaction)
	 */
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManagerListener#onFileManagerEvent(net.abachar.androftp.filelist.manager.FileManagerEvent)
	 */
	@Override
	public void onFileManagerEvent(FileManagerEvent fmEvent) {

		switch (fmEvent.getEvent()) {
			case FileManagerEvent.WILL_CONNECT:
				if (!mProgressDialog.isShowing()) {
					mProgressDialog.show();
				}
				break;

			case FileManagerEvent.DID_CONNECT:
				if (MainApplication.getInstance().isAllConnected()) {
					mProgressDialog.dismiss();
				}
				break;

			case FileManagerEvent.ERR_CONNECTION:
				new AlertDialog.Builder(this).setMessage("Erreur de connexion :(").setCancelable(true).setNeutralButton("Close", null).create().show();
				break;

			case FileManagerEvent.ERR_LOST_CONNECTION:
				new AlertDialog.Builder(this).setMessage("Connexion perdu :(").setCancelable(true).setNeutralButton("Close", null).create().show();
				break;
		}
	}

	/**
	 * @see android.app.Fragment#onCreateOptionsMenu(android.view.Menu,
	 *      android.view.MenuInflater)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		mSettingMenu = menu.add(0, 0, 10, R.string.menu_settings);
		mSettingMenu.setIcon(R.drawable.ic_action_settings);
		mSettingMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}

	/**
	 * @see android.app.Fragment#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (mSettingMenu == item) {
			startActivityForResult(new Intent(this, AndroFTPPreferenceActivity.class), 1);
			return true;
		}

		return false;
	}

	/**
	 * Tab tag
	 */
	private class TabTag {
		TabId tabId;
		String key;
		String className;

		TabTag(TabId tabId) {
			this.tabId = tabId;
			this.key = "andro-ftp-tab-index-" + tabId.ordinal();
			this.className = tabId.clazz.getName();
		}
	}

	/**
	 * Tab ID
	 */
	enum TabId {

		/** Local file manager fragment */
		LOCAL_MANAGER(LocalManagerFragment.class, R.string.main_tab_local),

		/** Server file manager fragment */
		SERVER_MANAGER(ServerManagerFragment.class, R.string.main_tab_server),

		/** Transfers fragment */
		TRANSFER_MANAGER(TransferFragment.class, R.string.main_tab_transfers);

		/** */
		final Class<? extends Fragment> clazz;

		/** */
		final int textId;

		/**
		 * 
		 * @param clazz
		 * @param textId
		 */
		private TabId(Class<? extends Fragment> clazz, int textId) {
			this.clazz = clazz;
			this.textId = textId;
		}
	}
}
