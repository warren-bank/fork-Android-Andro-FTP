package net.abachar.androftp.ui.fragment;

import net.abachar.androftp.R;
import net.abachar.androftp.filelist.FileEntry;
import net.abachar.androftp.filelist.FileManager;
import net.abachar.androftp.filelist.FileManagerListener;
import net.abachar.androftp.filelist.FileManagerMessage;
import net.abachar.androftp.filelist.OrderBy;
import net.abachar.androftp.ui.adapter.SmallFileAdapter;
import net.abachar.androftp.ui.adapter.WideFileAdapter;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

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
	protected TextView txtWideBrowserCWD;
	protected Spinner spnWideBrowserOrderBy;
	protected ListView lsvWideBrowserListFiles;
	protected WideFileAdapter wideBrowserFileAdapter;

	/**
	 * Small browser
	 */
	protected ImageButton btnSmallBrowserGoParent;
	protected TextView txtSmallBrowserCWD;
	protected ListView lsvSmallBrowserListFiles;
	protected SmallFileAdapter smallBrowserFileAdapter;

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
		wideBrowserFileManager.addFileManagerListener(this, FileManagerMessage.INITIAL_LIST_FILES, FileManagerMessage.BEGIN_UPDATE_LIST_FILES, FileManagerMessage.END_UPDATE_LIST_FILES);
		smallBrowserFileManager.addFileManagerListener(this, FileManagerMessage.INITIAL_LIST_FILES, FileManagerMessage.BEGIN_UPDATE_LIST_FILES, FileManagerMessage.END_UPDATE_LIST_FILES);

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
		wideBrowserFileAdapter = new WideFileAdapter(getActivity());
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

		// Setup wide explorer file list
		lsvSmallBrowserListFiles = (ListView) view.findViewById(R.id.small_browser_files);
		lsvSmallBrowserListFiles.setOnItemClickListener(this);

		// Setup wide explorer list adapter
		smallBrowserFileAdapter = new SmallFileAdapter(getActivity());
		lsvSmallBrowserListFiles.setAdapter(smallBrowserFileAdapter);
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
	 * @see net.abachar.androftp.filelist.FileManagerListener#onUpdateListFiles(net.abachar.androftp.filelist.FileManager,
	 *      net.abachar.androftp.filelist.FileManagerMessage)
	 */
	@Override
	public void onUpdateListFiles(FileManager fm, FileManagerMessage msg) {

		switch (msg) {
			case BEGIN_UPDATE_LIST_FILES:
				if (fm == wideBrowserFileManager) {
					txtWideBrowserCWD.setText(getString(R.string.loading));
					wideBrowserFileAdapter.setFiles(null);
					btnWideBrowserGoParent.setEnabled(false);
					chkWideBrowserSelectAll.setEnabled(false);
				} else if (fm == smallBrowserFileManager) {
					txtSmallBrowserCWD.setText(getString(R.string.loading));
					smallBrowserFileAdapter.setFiles(null);
					btnSmallBrowserGoParent.setEnabled(false);
				}
				break;

			case INITIAL_LIST_FILES:
			case END_UPDATE_LIST_FILES:
				if (fm == wideBrowserFileManager) {
					txtWideBrowserCWD.setText(fm.getCurrentPath());
					wideBrowserFileAdapter.setFiles(fm.getFiles());
					btnWideBrowserGoParent.setEnabled(wideBrowserFileManager.canGoParent());
					chkWideBrowserSelectAll.setEnabled((fm.getFiles() != null) && !fm.getFiles().isEmpty());
				} else if (fm == smallBrowserFileManager) {
					txtSmallBrowserCWD.setText(fm.getCurrentPath());
					smallBrowserFileAdapter.setFiles(fm.getFiles());
					btnSmallBrowserGoParent.setEnabled(smallBrowserFileManager.canGoParent());
				}
				break;
		}
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
		wideBrowserFileManager.setOrderBy(orderBy);
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
