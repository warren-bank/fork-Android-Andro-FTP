package net.abachar.androftp.filelist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.abachar.androftp.MainApplication;
import net.abachar.androftp.R;
import net.abachar.androftp.filelist.manager.FileEntry;
import net.abachar.androftp.filelist.manager.FileManager;
import net.abachar.androftp.filelist.manager.FileManagerEvent;
import net.abachar.androftp.filelist.manager.FileManagerListener;
import net.abachar.androftp.filelist.manager.OrderBy;
import net.abachar.androftp.transfers.manager.TransferManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ActionMode;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * 
 * @author abachar
 */
public abstract class AbstractManagerFragment extends Fragment implements FileManagerListener, OnClickListener, OnItemSelectedListener, OnItemClickListener {

	/** */
	protected final static int ID_MENU_UPLOAD = 0x01;
	protected final static int ID_MENU_DOWNLOAD = 0x02;
	protected final static int ID_MENU_DELETE = 0x03;
	protected final static int ID_MENU_RENAME = 0x04;
	protected final static int ID_MENU_DETAIL = 0x05;

	/** */
	protected int mFragmentLayoutId;

	/** Action mode */
	protected ActionMode mActionMode;
	protected boolean mMultiSelect;

	/** Managers */
	protected TransferManager mTransferManager;
	protected FileManager mWideBrowserFileManager;
	protected FileManager mSmallBrowserFileManager;

	/** Wide browser items */
	protected CheckBox mWideBrowserSelectAllCheckBox;
	protected ImageButton mWideBrowserGoParentButton;
	protected LinearLayout mWideBrowserLoadingProgress;
	protected TextView mWideBrowserCurrentDirectoryText;
	protected Spinner mWideBrowserOrderBySpinner;
	protected ListView mWideBrowserFileListView;
	protected WideFileAdapter mWideBrowserFileAdapter;

	/** Small browser items */
	protected ImageButton mSmallBrowserGoParentButton;
	protected LinearLayout mSmallBrowserLoadingProgress;
	protected TextView mSmallBrowserCurrentDirectoryText;
	protected ListView mSmallBrowserFileListView;
	protected SmallFileAdapter mSmallBrowserFileAdapter;

	/** Menus */
	protected MenuItem mRefreshMenu;
	protected MenuItem mNewfolderMenu;
	protected MenuItem mUploadMenu;
	protected MenuItem mDownloadMenu;
	protected MenuItem mDeleteMenu;
	protected MenuItem mRenameMenu;
	protected MenuItem mDetailMenu;

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
		View view = inflater.inflate(mFragmentLayoutId, container, false);

		// Initialize user interface
		initWideBrowserUI(view);
		initSmallBrowserUI(view);

		// Show files
		mWideBrowserFileManager.addListener(this);
		mSmallBrowserFileManager.addListener(this);

		// Transfer manager
		mTransferManager = MainApplication.getInstance().getTransferManager();

		// Return created view
		return view;
	}

	/**
	 * Setup wide browser
	 */
	private void initWideBrowserUI(View view) {

		// Setup wide explorer select all check box
		mWideBrowserSelectAllCheckBox = (CheckBox) view.findViewById(R.id.wide_browser_select_all);
		mWideBrowserSelectAllCheckBox.setOnClickListener(this);

		// Setup wide explorer go parent button
		mWideBrowserGoParentButton = (ImageButton) view.findViewById(R.id.wide_browser_go_parent);
		mWideBrowserGoParentButton.setOnClickListener(this);
		mWideBrowserGoParentButton.setEnabled(mWideBrowserFileManager.canGoParent());

		// Loading progress bar
		mWideBrowserLoadingProgress = (LinearLayout) view.findViewById(R.id.wide_browser_loading_progress);
		mWideBrowserLoadingProgress.setVisibility(View.GONE);

		// Setup wide explorer current working directory text
		mWideBrowserCurrentDirectoryText = (TextView) view.findViewById(R.id.wide_browser_cwd);

		// Setup wide explorer order by spinner
		ArrayAdapter<CharSequence> wideBrowserOrderByAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.order_by_list, android.R.layout.simple_spinner_item);
		wideBrowserOrderByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mWideBrowserOrderBySpinner = (Spinner) view.findViewById(R.id.wide_browser_order_by);
		mWideBrowserOrderBySpinner.setAdapter(wideBrowserOrderByAdapter);
		mWideBrowserOrderBySpinner.setOnItemSelectedListener(this);

		// Setup wide explorer file list
		mWideBrowserFileListView = (ListView) view.findViewById(R.id.wide_browser_files);
		mWideBrowserFileListView.setOnItemClickListener(this);

		// Setup wide explorer list adapter
		mWideBrowserFileAdapter = new WideFileAdapter(getActivity(), this);
		mWideBrowserFileListView.setAdapter(mWideBrowserFileAdapter);
	}

	/**
	 * Setup small browser
	 */
	private void initSmallBrowserUI(View view) {

		// Setup wide explorer go parent button
		mSmallBrowserGoParentButton = (ImageButton) view.findViewById(R.id.small_browser_go_parent);
		mSmallBrowserGoParentButton.setOnClickListener(this);
		mSmallBrowserGoParentButton.setEnabled(mSmallBrowserFileManager.canGoParent());

		// Setup wide explorer current working directory text
		mSmallBrowserCurrentDirectoryText = (TextView) view.findViewById(R.id.small_browser_cwd);

		// Loading progress bar
		mSmallBrowserLoadingProgress = (LinearLayout) view.findViewById(R.id.small_browser_loading_progress);
		mSmallBrowserLoadingProgress.setVisibility(View.GONE);

		// Setup wide explorer file list
		mSmallBrowserFileListView = (ListView) view.findViewById(R.id.small_browser_files);
		mSmallBrowserFileListView.setOnItemClickListener(this);

		// Setup wide explorer list adapter
		mSmallBrowserFileAdapter = new SmallFileAdapter(getActivity());
		mSmallBrowserFileListView.setAdapter(mSmallBrowserFileAdapter);
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

		// New folder
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
			mWideBrowserFileManager.refresh();
			mSmallBrowserFileManager.refresh();
			return true;
		}

		if (mNewfolderMenu == item) {
			onWideBrowserCreateNewFolder();
			return true;
		}

		return false;
	}

	/**
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View view) {

		int selectedCount, count;

		switch (view.getId()) {

			case R.id.wide_browser_go_parent:
				onWideBrowserGoParentButtonClick();
				break;

			case R.id.small_browser_go_parent:
				onSmallBrowserGoParentButtonClick();
				break;

			case R.id.wide_browser_select_all:
				/* Event trigged from select all */
				boolean checked = mWideBrowserSelectAllCheckBox.isChecked();
				mWideBrowserFileAdapter.updateSelectAllFiles(checked);
				count = mWideBrowserFileAdapter.getCount();
				updateActionMode(checked ? count : 0, count);
				break;

			default:
				count = mWideBrowserFileAdapter.getCount();
				selectedCount = mWideBrowserFileAdapter.getSelectedCount();
				mWideBrowserSelectAllCheckBox.setChecked(selectedCount == count);
				updateActionMode(selectedCount, count);
				break;
		}
	}

	/**
	 * 
	 * @param selectedCount
	 * @param count
	 */
	private void updateActionMode(int selectedCount, int count) {

		// Manage action mode mode
		if (selectedCount == 0) {
			if (mActionMode != null) {
				mActionMode.finish();
			}
		} else {
			mMultiSelect = selectedCount > 1;

			if (mActionMode == null) {
				mActionMode = getActivity().startActionMode(mSelectAction);
			} else {
				mActionMode.invalidate();
			}

			if (!mMultiSelect) {
				mActionMode.setTitle(getString(R.string.msg_1_element_selected));
			} else {
				mActionMode.setTitle(getString(R.string.msg_n_element_selected, selectedCount));
			}
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

		if (listView == mWideBrowserFileListView) {
			onWideBrowserListItemClick(view, position, id);
			mWideBrowserGoParentButton.setEnabled(true);
		} else if (listView == mSmallBrowserFileListView) {
			onSmallBrowserListItemClick(view, position, id);
			mSmallBrowserGoParentButton.setEnabled(true);
		}
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManagerListener#onFileManagerEvent(net.abachar.androftp.filelist.manager.FileManagerEvent)
	 */
	@Override
	public void onFileManagerEvent(FileManagerEvent fmEvent) {

		FileManager fmSource = fmEvent.getSource();

		switch (fmEvent.getEvent()) {
			case FileManagerEvent.WILL_LOAD_LIST_FILES:
				if (fmSource == mWideBrowserFileManager) {
					mWideBrowserCurrentDirectoryText.setText(getString(R.string.loading));
					mWideBrowserFileAdapter.setFiles(null);
					mWideBrowserGoParentButton.setVisibility(View.GONE);
					mWideBrowserLoadingProgress.setVisibility(View.VISIBLE);
					mWideBrowserSelectAllCheckBox.setEnabled(false);
				} else if (fmSource == mSmallBrowserFileManager) {
					mSmallBrowserCurrentDirectoryText.setText(getString(R.string.loading));
					mSmallBrowserFileAdapter.setFiles(null);
					mSmallBrowserGoParentButton.setVisibility(View.GONE);
					mSmallBrowserLoadingProgress.setVisibility(View.VISIBLE);
				}
				break;

			case FileManagerEvent.INITIAL_LIST_FILES:
				if (fmSource == mWideBrowserFileManager) {
					mWideBrowserCurrentDirectoryText.setText(fmSource.getCurrentPath());
					mWideBrowserFileAdapter.setFiles(null);
					mWideBrowserLoadingProgress.setVisibility(View.GONE);
					mWideBrowserGoParentButton.setVisibility(View.VISIBLE);
					mWideBrowserGoParentButton.setEnabled(mWideBrowserFileManager.canGoParent());
					mWideBrowserSelectAllCheckBox.setEnabled(false);
				} else if (fmSource == mSmallBrowserFileManager) {
					mSmallBrowserCurrentDirectoryText.setText(fmSource.getCurrentPath());
					mSmallBrowserFileAdapter.setFiles(null);
					mSmallBrowserLoadingProgress.setVisibility(View.GONE);
					mSmallBrowserGoParentButton.setVisibility(View.VISIBLE);
					mSmallBrowserGoParentButton.setEnabled(mSmallBrowserFileManager.canGoParent());
				}
				break;

			case FileManagerEvent.DID_LOAD_LIST_FILES:
				if (fmEvent.getSource() == mWideBrowserFileManager) {
					mWideBrowserCurrentDirectoryText.setText(fmSource.getCurrentPath());
					mWideBrowserFileAdapter.setFiles(fmSource.getFiles());
					mWideBrowserLoadingProgress.setVisibility(View.GONE);
					mWideBrowserGoParentButton.setVisibility(View.VISIBLE);
					mWideBrowserGoParentButton.setEnabled(mWideBrowserFileManager.canGoParent());
					mWideBrowserSelectAllCheckBox.setEnabled((fmSource.getFiles() != null) && !fmSource.getFiles().isEmpty());
				} else if (fmEvent.getSource() == mSmallBrowserFileManager) {
					mSmallBrowserCurrentDirectoryText.setText(fmSource.getCurrentPath());
					mSmallBrowserFileAdapter.setFiles(fmSource.getFiles());
					mSmallBrowserLoadingProgress.setVisibility(View.GONE);
					mSmallBrowserGoParentButton.setVisibility(View.VISIBLE);
					mSmallBrowserGoParentButton.setEnabled(mSmallBrowserFileManager.canGoParent());
				}
				break;
		}
	}

	/**
	 * 
	 * @param directory
	 *            directory path to create the new folder in.
	 */
	public void onWideBrowserCreateNewFolder() {

		AlertDialog.Builder prompt = new AlertDialog.Builder(getActivity());

		// Title and message
		prompt.setTitle("Create a new Folder");

		// Set an EditText view to get user input
		final EditText input = new EditText(getActivity());
		prompt.setView(input);

		prompt.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String name = input.getText().toString();
				if (name.length() > 0) {
					mWideBrowserFileManager.createNewfolder(name);
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
	 */
	protected void onWideBrowserGoParentButtonClick() {
		if (mActionMode != null) {
			mActionMode.finish();
		}

		mWideBrowserFileManager.changeToParentDirectory();
	}

	/**
	 * 
	 * @param orderBy
	 */
	protected void onWideBrowserOrderByChange(OrderBy orderBy) {
		mWideBrowserFileManager.changeOrderBy(orderBy);
	}

	/**
	 * 
	 * @param view
	 * @param position
	 * @param id
	 */
	protected void onWideBrowserListItemClick(View view, int position, long id) {
		if (mActionMode != null) {
			mActionMode.finish();
		}

		FileEntry file = (FileEntry) mWideBrowserFileAdapter.getItem(position);
		if (file.isFolder()) {
			mWideBrowserFileManager.changeWorkingDirectory(file.getName());
		}
	}

	/**
	 * 
	 */
	protected void onSmallBrowserGoParentButtonClick() {
		mSmallBrowserFileManager.changeToParentDirectory();
	}

	/**
	 * 
	 * @param view
	 * @param position
	 * @param id
	 */
	protected void onSmallBrowserListItemClick(View view, int position, long id) {

		FileEntry file = (FileEntry) mSmallBrowserFileAdapter.getItem(position);
		if (file.isFolder()) {
			mSmallBrowserFileManager.changeWorkingDirectory(file.getName());
		}
	}

	/**
	 * 
	 */
	protected void onMenuDelete() {

		final List<FileEntry> selectedFiles = mWideBrowserFileAdapter.getSelectedFiles();
		int count = selectedFiles.size();

		// Ask yes ?
		AlertDialog.Builder prompt = new AlertDialog.Builder(getActivity());
		prompt.setTitle("Delete");
		if (count == 1) {
			prompt.setMessage("This item will be removed");
		} else {
			prompt.setMessage(count + " items will be removed");
		}

		prompt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				mWideBrowserFileManager.deleteFiles(selectedFiles);
				mActionMode.finish();
			}
		});

		prompt.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});

		prompt.show();
	}

	/**
	 * 
	 */
	protected void onMenuRename() {

		AlertDialog.Builder prompt = new AlertDialog.Builder(getActivity());

		// Title and message
		prompt.setTitle("Rename");

		// Set an EditText view to get user input
		FileEntry fileEntry = mWideBrowserFileAdapter.getFirstSelectedFile();
		final String fileName = fileEntry.getName();
		final int lastIndexOf = fileName.lastIndexOf(".");
		final String oldName = (lastIndexOf != -1) ? fileName.substring(0, lastIndexOf) : fileName;
		final String fileExt = (lastIndexOf != -1) ? fileName.substring(lastIndexOf) : "";
		final EditText input = new EditText(getActivity());
		input.setText(oldName);
		input.selectAll();
		prompt.setView(input);

		prompt.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String newName = input.getText().toString();
				if ((newName.length() > 0) && !newName.equals(oldName)) {
					mWideBrowserFileManager.renameFile(fileName, newName + fileExt);
					mActionMode.finish();
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
	 */
	protected void onMenuDetail() {

		// File data
		FileEntry fileEntry = mWideBrowserFileAdapter.getFirstSelectedFile();
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();

		Map<String, String> row = new HashMap<String, String>(2);
		row.put("label", "Name");
		row.put("value", fileEntry.getName());
		data.add(row);

		row = new HashMap<String, String>(2);
		row.put("label", "Format");
		row.put("value", fileEntry.getType().getFormat());
		data.add(row);

		row = new HashMap<String, String>(2);
		row.put("label", "Location");
		row.put("value", fileEntry.getParentPath());
		data.add(row);

		row = new HashMap<String, String>(2);
		row.put("label", "Date of last modification");
		row.put("value", new SimpleDateFormat("dd/MM/yyyy hh:mm").format(new Date(fileEntry.getLastModified())));
		data.add(row);

		// Create listview
		ListView listView = new ListView(getActivity());
		listView.setAdapter(new SimpleAdapter(getActivity(), data, android.R.layout.simple_list_item_2, new String[] { "label", "value" }, new int[] { android.R.id.text1, android.R.id.text2 }));

		// Title and message
		AlertDialog.Builder prompt = new AlertDialog.Builder(getActivity());
		prompt.setTitle("Details");
		prompt.setView(listView);

		prompt.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				mActionMode.finish();
			}
		});

		prompt.show();
	}

	/**
	 * 
	 */
	protected abstract void onMenuTransfer();

	/**
	 * 
	 */
	protected ActionMode.Callback mSelectAction = new ActionMode.Callback() {

		/**
		 * @see android.view.ActionMode.Callback#onCreateActionMode(android.view.ActionMode,
		 *      android.view.Menu)
		 */
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			return AbstractManagerFragment.this.onCreateActionMode(menu);
		}

		/**
		 * @see android.view.ActionMode.Callback#onPrepareActionMode(android.view.ActionMode,
		 *      android.view.Menu)
		 */
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return AbstractManagerFragment.this.onPrepareActionMode(menu);
		}

		/**
		 * @see android.view.ActionMode.Callback#onActionItemClicked(android.view.ActionMode,
		 *      android.view.MenuItem)
		 */
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

			switch (item.getItemId()) {

				case ID_MENU_UPLOAD:
				case ID_MENU_DOWNLOAD:
					onMenuTransfer();
					return true;

				case ID_MENU_DELETE:
					onMenuDelete();
					return true;

				case ID_MENU_RENAME:
					onMenuRename();
					return true;

				case ID_MENU_DETAIL:
					onMenuDetail();
					return true;
			}

			return false;
		}

		/**
		 * @see android.view.ActionMode.Callback#onDestroyActionMode(android.view.ActionMode)
		 */
		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mActionMode = null;
			mWideBrowserSelectAllCheckBox.setChecked(false);
			mWideBrowserFileAdapter.updateSelectAllFiles(false);
		}
	};

	/**
	 * 
	 * @param menu
	 * @return
	 */
	protected boolean onCreateActionMode(Menu menu) {

		// Menu delete
		mDeleteMenu = menu.add(Menu.NONE, ID_MENU_DELETE, Menu.NONE, R.string.menu_delete);
		mDeleteMenu.setIcon(R.drawable.ic_action_delete);
		mDeleteMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		// Menu rename
		mRenameMenu = menu.add(Menu.NONE, ID_MENU_RENAME, Menu.NONE, R.string.menu_rename);
		mRenameMenu.setIcon(R.drawable.ic_action_rename);
		mRenameMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		// Menu detail
		mDetailMenu = menu.add(Menu.NONE, ID_MENU_DETAIL, Menu.NONE, R.string.menu_detail);
		mDetailMenu.setIcon(R.drawable.ic_action_detail);
		mDetailMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		return true;
	}

	/**
	 * 
	 * @param menu
	 * @return
	 */
	protected boolean onPrepareActionMode(Menu menu) {

		// Enable/disable detail and rename actions
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
}
