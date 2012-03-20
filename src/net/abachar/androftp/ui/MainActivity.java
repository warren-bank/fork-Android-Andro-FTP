package net.abachar.androftp.ui;

import net.abachar.androftp.R;
import net.abachar.androftp.filelist.FTPFileManager;
import net.abachar.androftp.filelist.FileManager;
import net.abachar.androftp.filelist.LocalFileManager;
import net.abachar.androftp.filelist.OrderBy;
import net.abachar.androftp.servers.Logontype;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Environment;

/**
 * 
 * @author abachar
 */
public class MainActivity extends Activity implements ActionBar.TabListener {

	/** Tab indexs and selected tab index */
	private TabId selectedTab;

	/** File manages */
	private FileManager localFileManager;
	private FileManager serverFileManager;

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Use main view
		setContentView(R.layout.main);
		
		getIntent().putExtra("host", "localhost");
		getIntent().putExtra("port", 21);
		getIntent().putExtra("logontype", Logontype.NORMAL);
		getIntent().putExtra("username", "abachar");
		getIntent().putExtra("password", "z6tonbjn");
		
		// Create map properties
		Bundle bundle = new Bundle();
		if (savedInstanceState != null) {
			
		} else {

			// Setup local root directory
			String path = Environment.getExternalStorageDirectory().getAbsolutePath();
			bundle.putString("local.rootPath", path);
			bundle.putString("local.currentPath", path);
			
			// Server data
			Bundle intentExtras = getIntent().getExtras();
			bundle.putString("server.host", intentExtras.getString("host"));
			bundle.putInt("server.port", intentExtras.getInt("port"));
			Logontype logontype = (Logontype) intentExtras.get("logontype");
			bundle.putSerializable("server.logontype", logontype);
			if (logontype == Logontype.NORMAL) {
				bundle.putString("server.username", bundle.getString("username"));
				bundle.putString("server.password", bundle.getString("password"));
			}

			// Setup local and server order
			bundle.putSerializable("local.orderBy", OrderBy.NAME);
			bundle.putSerializable("server.orderBy", OrderBy.NAME);

			// Setup selected tab
			selectedTab = TabId.LOCAL_MANAGER;
		}

		// File managers
		localFileManager = new LocalFileManager(bundle);
		serverFileManager = new FTPFileManager(bundle);

		// Setup actionbar
		setupActionBar();
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
		createTab(TabId.LOCAL_MANAGER);
		createTab(TabId.SERVER_MANAGER);
		createTab(TabId.TRANSFER_MANAGER);
		createTab(TabId.CONSOLE);

		// Set selected tab
		actionBar.setSelectedNavigationItem(selectedTab.ordinal());
	}

	/**
	 * 
	 * @param index
	 * @param className
	 * @param textId
	 */
	private void createTab(TabId tabId) {

		ActionBar actionBar = getActionBar();
		ActionBar.Tab tab = actionBar.newTab();

		tab.setText(getString(tabId.getTextId()));
		tab.setTag(new TabTag(tabId));
		tab.setTabListener(this);

		actionBar.addTab(tab);
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
		// int index;
		String key;
		String className;

		TabTag(TabId tabId) {
			// this.index = index;
			this.key = "andro-ftp-tab-index-" + tabId.ordinal();
			this.className = tabId.getClazz().getName();
		}
	}
}
