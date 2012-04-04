package net.abachar.androftp;

import net.abachar.androftp.R;
import net.abachar.androftp.filelist.manager.FileManager;
import net.abachar.androftp.filelist.manager.FileManagerListener;
import net.abachar.androftp.filelist.manager.FileManagerEvent;
import net.abachar.androftp.filelist.manager.LocalFileManager;
import net.abachar.androftp.servers.Logontype;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.Bundle;

/**
 * 
 * @author abachar
 */
public class MainActivity extends Activity implements ActionBar.TabListener, FileManagerListener {

	/** Tab indexs and selected tab index */
	private TabId selectedTab;

	/** File manages */
	private FileManager localFileManager;
	private FileManager serverFileManager;

	/** Connexion progress dialog */
	private ProgressDialog connectProgress;

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Show waiting dialog
		connectProgress = ProgressDialog.show(this, getString(R.string.connect_progress_title), getString(R.string.connect_progress_message), true, false);

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
			selectedTab = TabId.LOCAL_MANAGER;
		}

		// Instanciate managers
		localFileManager = new LocalFileManager(this);
		serverFileManager = new LocalFileManager(this); // new FTPFileManager(this);

		// Listener
		FileManagerEvent[] msgs = { FileManagerEvent.WILL_CONNECT, FileManagerEvent.DID_CONNECT, FileManagerEvent.ERROR_CONNECTION, FileManagerEvent.LOST_CONNECTION };
		localFileManager.addFileManagerListener(this, msgs);
		serverFileManager.addFileManagerListener(this, msgs);

		// Init file managers
		localFileManager.init(bundle);
		serverFileManager.init(bundle);

		// Use main view
		setContentView(R.layout.main);

		// Setup actionbar
		setupActionBar();

		// Connect file managers
		localFileManager.connect();
		serverFileManager.connect();
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
			tab.setText(getString(tabId.getTextId()));
			tab.setTag(new TabTag(tabId));
			tab.setTabListener(this);

			actionBar.addTab(tab);
		}

		// Set selected tab
		actionBar.setSelectedNavigationItem(selectedTab.ordinal());
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
		selectedTab = tag.tabId;
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
	 * @see net.abachar.androftp.filelist.manager.FileManagerListener#onFileManagerEvent(net.abachar.androftp.filelist.manager.FileManager,
	 *      net.abachar.androftp.filelist.manager.FileManagerEvent)
	 */
	@Override
	public void onFileManagerEvent(FileManager fm, FileManagerEvent msg) {

		switch (msg) {
			case WILL_CONNECT:
				if (!connectProgress.isShowing()) {
					connectProgress.show();
				}
				break;

			case DID_CONNECT:
				if (localFileManager.isConnected() && serverFileManager.isConnected()) {
					connectProgress.dismiss();
				}
				break;

			case ERROR_CONNECTION:
				new AlertDialog.Builder(this).setMessage("Erreur de connexion :(").setCancelable(true).setNeutralButton("Close", null).create().show();
				break;

			case LOST_CONNECTION:
				new AlertDialog.Builder(this).setMessage("Connexion perdu :(").setCancelable(true).setNeutralButton("Close", null).create().show();
				break;
		}
	}

	/**
	 * @return the localFileManager
	 */
	public FileManager getLocalFileManager() {
		return localFileManager;
	}

	/**
	 * @return the serverFileManager
	 */
	public FileManager getServerFileManager() {
		return serverFileManager;
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
			this.className = tabId.getClazz().getName();
		}
	}
}
