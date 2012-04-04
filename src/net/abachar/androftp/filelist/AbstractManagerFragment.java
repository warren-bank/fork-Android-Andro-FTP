package net.abachar.androftp.filelist;

import net.abachar.androftp.R;
import net.abachar.androftp.filelist.manager.FileEntry;
import net.abachar.androftp.filelist.manager.FileManager;
import net.abachar.androftp.filelist.manager.FileManagerListener;
import net.abachar.androftp.filelist.manager.FileManagerEvent;
import net.abachar.androftp.filelist.manager.OrderBy;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;


//<item
//android:id="@+id/menu_upload"
//android:icon="@drawable/ic_action_upload"
//android:showAsAction="ifRoom"
//android:title="@string/menu_upload"/>
//<item
//android:id="@+id/menu_download"
//android:icon="@drawable/ic_action_download"
//android:showAsAction="ifRoom"
//android:title="@string/menu_download"/>
//<item
//android:id="@+id/menu_delete"
//android:icon="@drawable/ic_action_delete"
//android:showAsAction="ifRoom"
//android:title="@string/menu_delete"/>
//
//
//<item
//android:id="@+id/menu_rename"
//android:icon="@drawable/ic_action_rename"
//android:showAsAction="ifRoom"
//android:title="@string/menu_rename"/>
//<item
//android:id="@+id/menu_detail"
//android:icon="@drawable/ic_action_detail"
//android:showAsAction="ifRoom"
//android:title="@string/menu_detail"/>

/**
 * 
 * @author abachar
 */
public abstract class AbstractManagerFragment extends Fragment implements FileManagerListener, OnCheckedChangeListener, OnClickListener, OnItemSelectedListener, OnItemClickListener {

	/**
	 * File manager
	 */
	protected FileManager wideBrowserFileManager;
	protected FileManager smallBrowserFileManager;

	/**
	 * Wide browser
	 */
	protected CheckBox chkWideBrowserSelectAll;
	protected ImageButton btnWideBrowserGoParent;
	protected LinearLayout lytWideBrowserLoadingProgress;
	protected TextView txtWideBrowserCWD;
	protected Spinner spnWideBrowserOrderBy;
	protected ListView lsvWideBrowserListFiles;
	protected WideFileAdapter wideBrowserFileAdapter;

	/**
	 * Small browser
	 */
	protected ImageButton btnSmallBrowserGoParent;
	protected LinearLayout lytSmallBrowserLoadingProgress;
	protected TextView txtSmallBrowserCWD;
	protected ListView lsvSmallBrowserListFiles;
	protected SmallFileAdapter smallBrowserFileAdapter;

	/**
	 * Menus
	 */
	protected MenuItem mRefreshMenu;
	protected MenuItem mNewfolderMenu;

	/**
	 * @see android.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	/**
	 * Setup fragment view
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// Inflate browser view
		View view = inflater.inflate(R.layout.fragment_browsers, container, false);

		// Initialize user interface
		initWideBrowserUI(view);
		initSmallBrowserUI(view);

		// Show files
		wideBrowserFileManager.addFileManagerListener(this, FileManagerEvent.INITIAL_LIST_FILES, FileManagerEvent.WILL_LOAD_LIST_FILES, FileManagerEvent.DID_LOAD_LIST_FILES);
		smallBrowserFileManager.addFileManagerListener(this, FileManagerEvent.INITIAL_LIST_FILES, FileManagerEvent.WILL_LOAD_LIST_FILES, FileManagerEvent.DID_LOAD_LIST_FILES);

		// Return created view
		return view;
	}

	/**
	 * Setup wide browser
	 */
	private void initWideBrowserUI(View view) {

		// Setup wide explorer select all check box
		chkWideBrowserSelectAll = (CheckBox) view.findViewById(R.id.wide_browser_select_all);
		chkWideBrowserSelectAll.setOnCheckedChangeListener(this);

		// Setup wide explorer go parent button
		btnWideBrowserGoParent = (ImageButton) view.findViewById(R.id.wide_browser_go_parent);
		btnWideBrowserGoParent.setOnClickListener(this);
		btnWideBrowserGoParent.setEnabled(wideBrowserFileManager.canGoParent());

		// Loading progress bar
		lytWideBrowserLoadingProgress = (LinearLayout) view.findViewById(R.id.wide_browser_loading_progress);
		lytWideBrowserLoadingProgress.setVisibility(View.GONE);

		// Setup wide explorer current working directory text
		txtWideBrowserCWD = (TextView) view.findViewById(R.id.wide_browser_cwd);

		// Setup wide explorer order by spinner
		ArrayAdapter<CharSequence> wideBrowserOrderByAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.order_by_list, android.R.layout.simple_spinner_item);
		wideBrowserOrderByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnWideBrowserOrderBy = (Spinner) view.findViewById(R.id.wide_browser_order_by);
		spnWideBrowserOrderBy.setAdapter(wideBrowserOrderByAdapter);
		spnWideBrowserOrderBy.setOnItemSelectedListener(this);

		// Setup wide explorer file list
		lsvWideBrowserListFiles = (ListView) view.findViewById(R.id.wide_browser_files);
		lsvWideBrowserListFiles.setOnItemClickListener(this);

		// Setup wide explorer list adapter
		wideBrowserFileAdapter = new WideFileAdapter(this);
		lsvWideBrowserListFiles.setAdapter(wideBrowserFileAdapter);
	}

	/**
	 * Setup small browser
	 */
	private void initSmallBrowserUI(View view) {

		// Setup wide explorer go parent button
		btnSmallBrowserGoParent = (ImageButton) view.findViewById(R.id.small_browser_go_parent);
		btnSmallBrowserGoParent.setOnClickListener(this);
		btnSmallBrowserGoParent.setEnabled(smallBrowserFileManager.canGoParent());

		// Setup wide explorer current working directory text
		txtSmallBrowserCWD = (TextView) view.findViewById(R.id.small_browser_cwd);

		// Loading progress bar
		lytSmallBrowserLoadingProgress = (LinearLayout) view.findViewById(R.id.small_browser_loading_progress);
		lytSmallBrowserLoadingProgress.setVisibility(View.GONE);

		// Setup wide explorer file list
		lsvSmallBrowserListFiles = (ListView) view.findViewById(R.id.small_browser_files);
		lsvSmallBrowserListFiles.setOnItemClickListener(this);

		// Setup wide explorer list adapter
		smallBrowserFileAdapter = new SmallFileAdapter(this);
		lsvSmallBrowserListFiles.setAdapter(smallBrowserFileAdapter);
	}

	/**
	 * @see android.app.Fragment#onCreateOptionsMenu(android.view.Menu,
	 *      android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		// Refresh
		mRefreshMenu = menu.add(R.string.menu_refresh);
		mRefreshMenu.setIcon(R.drawable.ic_action_refresh);
		mRefreshMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		// Refresh & New folder
		mNewfolderMenu = menu.add(R.string.menu_newfolder);
		mNewfolderMenu.setIcon(R.drawable.ic_action_newfolder);
		mNewfolderMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	}

	/**
	 * @see android.app.Fragment#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (mRefreshMenu == item) {
			wideBrowserFileManager.refresh();
			smallBrowserFileManager.refresh();
			return true;
		}

		if (mNewfolderMenu == item) {
			onWideBrowserCreateNewFolder();
			return true;
		}

		return false;
	}

	/**
	 * @see android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged(android.widget.CompoundButton,
	 *      boolean)
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		onWideBrowserSelectAllCheckedChanged(isChecked);
	}

	/**
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View view) {

		if (view == btnWideBrowserGoParent) {
			onWideBrowserGoParentButtonClick();
		} else if (view == btnSmallBrowserGoParent) {
			onSmallBrowserGoParentButtonClick();
		}
	}

	/**
	 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView,
	 *      android.view.View, int, long)
	 */
	@Override
	public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		onWideBrowserOrderByChange(OrderBy.getByOrdinal(position));
	}

	/**
	 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android.widget.AdapterView)
	 */
	@Override
	public void onNothingSelected(AdapterView<?> parentView) {
		// Todo nothings
	}

	/**
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView,
	 *      android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> listView, View view, int position, long id) {

		if (listView == lsvWideBrowserListFiles) {
			onWideBrowserListItemClick(view, position, id);
			btnWideBrowserGoParent.setEnabled(true);
		} else if (listView == lsvSmallBrowserListFiles) {
			onSmallBrowserListItemClick(view, position, id);
			btnSmallBrowserGoParent.setEnabled(true);
		}
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManagerListener#onFileManagerEvent(net.abachar.androftp.filelist.manager.FileManager,
	 *      net.abachar.androftp.filelist.manager.FileManagerEvent)
	 */
	@Override
	public void onFileManagerEvent(FileManager fm, FileManagerEvent msg) {

		switch (msg) {
			case WILL_LOAD_LIST_FILES:
				if (fm == wideBrowserFileManager) {
					txtWideBrowserCWD.setText(getString(R.string.loading));
					wideBrowserFileAdapter.setFiles(null);
					btnWideBrowserGoParent.setVisibility(View.GONE);
					lytWideBrowserLoadingProgress.setVisibility(View.VISIBLE);
					chkWideBrowserSelectAll.setEnabled(false);
				} else if (fm == smallBrowserFileManager) {
					txtSmallBrowserCWD.setText(getString(R.string.loading));
					smallBrowserFileAdapter.setFiles(null);
					btnSmallBrowserGoParent.setVisibility(View.GONE);
					lytSmallBrowserLoadingProgress.setVisibility(View.VISIBLE);
				}
				break;

			case INITIAL_LIST_FILES:
			case DID_LOAD_LIST_FILES:
				if (fm == wideBrowserFileManager) {
					txtWideBrowserCWD.setText(fm.getCurrentPath());
					wideBrowserFileAdapter.setFiles(fm.getFiles());
					lytWideBrowserLoadingProgress.setVisibility(View.GONE);
					btnWideBrowserGoParent.setVisibility(View.VISIBLE);
					btnWideBrowserGoParent.setEnabled(wideBrowserFileManager.canGoParent());
					chkWideBrowserSelectAll.setEnabled((fm.getFiles() != null) && !fm.getFiles().isEmpty());
				} else if (fm == smallBrowserFileManager) {
					txtSmallBrowserCWD.setText(fm.getCurrentPath());
					smallBrowserFileAdapter.setFiles(fm.getFiles());
					lytSmallBrowserLoadingProgress.setVisibility(View.GONE);
					btnSmallBrowserGoParent.setVisibility(View.VISIBLE);
					btnSmallBrowserGoParent.setEnabled(smallBrowserFileManager.canGoParent());
				}
				break;
		}
	}

	/**
	 * 
	 * @param directory directory path to create the new folder in.
	 */
	public void onWideBrowserCreateNewFolder() {
		
		AlertDialog.Builder prompt = new AlertDialog.Builder(getActivity());
		
		// Title and message
		prompt.setTitle("Create a new Folder");
		prompt.setMessage("Type the name of the folder you would like to create.");
		
		// Set an EditText view to get user input 
		final EditText input = new EditText(getActivity());
		prompt.setView(input);
		
		prompt.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String name = input.getText().toString();
				if (name.length() > 0) {
					wideBrowserFileManager.createNewfolder(name);
					dialog.dismiss();
				}
			}
		});

		prompt.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});

		prompt.show();
	}
	
	/**
	 * 
	 * @param isChecked
	 */
	protected void onWideBrowserSelectAllCheckedChanged(boolean isChecked) {
		wideBrowserFileAdapter.selectAllFiles(isChecked);
	}

	/**
	 * 
	 */
	protected void onWideBrowserGoParentButtonClick() {
		wideBrowserFileManager.changeToParentDirectory();
	}

	/**
	 * 
	 * @param orderBy
	 */
	protected void onWideBrowserOrderByChange(OrderBy orderBy) {
		wideBrowserFileManager.changeOrderBy(orderBy);
	}

	/**
	 * 
	 * @param view
	 * @param position
	 * @param id
	 */
	protected void onWideBrowserListItemClick(View view, int position, long id) {

		FileEntry file = (FileEntry) wideBrowserFileAdapter.getItem(position);
		if (file.isFolder()) {
			wideBrowserFileManager.changeWorkingDirectory(file.getName());
		}
	}

	/**
	 * 
	 */
	protected void onSmallBrowserGoParentButtonClick() {
		smallBrowserFileManager.changeToParentDirectory();
	}

	/**
	 * 
	 * @param view
	 * @param position
	 * @param id
	 */
	protected void onSmallBrowserListItemClick(View view, int position, long id) {

		FileEntry file = (FileEntry) smallBrowserFileAdapter.getItem(position);
		if (file.isFolder()) {
			smallBrowserFileManager.changeWorkingDirectory(file.getName());
		}
	}
}
