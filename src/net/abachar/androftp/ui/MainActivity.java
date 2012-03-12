package net.abachar.androftp.ui;

import java.util.HashMap;
import java.util.Map;

import net.abachar.androftp.R;
import net.abachar.androftp.filelist.FileManager;
import net.abachar.androftp.filelist.LocalFileManager;
import net.abachar.androftp.filelist.OrderBy;
import net.abachar.androftp.ui.fragment.LocalManagerFragment;
import net.abachar.androftp.ui.fragment.ServerManagerFragment;
import net.abachar.androftp.ui.fragment.TransferFragment;
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
	public final static int LOCAL_MANAGER_TAB = 0;
	public final static int SERVER_MANAGER_TAB = 1;
	public final static int TRANSFER_MANAGER_TAB = 2;
	private int selectedTab;

	/** File manages */
	private LocalFileManager localFileManager;

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Use main view
		setContentView(R.layout.main);
		
		// Create map properties
		Map<String, String> data = new HashMap<String, String>();
		if (savedInstanceState != null) {
			
		} else {

			// Setup local root directory
			String path = Environment.getExternalStorageDirectory().getAbsolutePath();
			data.put("local.rootPath", path);
			data.put("local.currentPath", path);

			// Setup local and server order
			data.put("local.orderBy", OrderBy.NAME.toString());

			// Setup selected tab
			selectedTab = LOCAL_MANAGER_TAB;
		}

		localFileManager = new LocalFileManager(data);

		// Setup tab
		ActionBar actionBar = getActionBar();
		// actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM |
		// ActionBar.DISPLAY_USE_LOGO);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);

		// Create local tab
		createTab(LOCAL_MANAGER_TAB, LocalManagerFragment.class.getName(), R.string.main_tab_local);
		createTab(SERVER_MANAGER_TAB, ServerManagerFragment.class.getName(), R.string.main_tab_server);
		createTab(TRANSFER_MANAGER_TAB, TransferFragment.class.getName(), R.string.main_tab_transfers);

		// Set selected tab
		actionBar.setSelectedNavigationItem(selectedTab);
	}

	/**
	 * 
	 * @param index
	 * @param className
	 * @param textId
	 */
	private void createTab(int index, String className, int textId) {

		ActionBar actionBar = getActionBar();
		ActionBar.Tab tab = actionBar.newTab();

		tab.setText(getString(textId));
		tab.setTag(new TabTag(index, className));
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
	 * Tab tag
	 */
	private class TabTag {
		// int index;
		String key;
		String className;

		TabTag(int index, String className) {
			// this.index = index;
			this.key = "andro-ftp-tab-index-" + index;
			this.className = className;
		}
	}
}
