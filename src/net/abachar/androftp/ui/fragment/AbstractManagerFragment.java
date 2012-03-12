package net.abachar.androftp.ui.fragment;

import net.abachar.androftp.R;
import net.abachar.androftp.filelist.FileManager;
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
public abstract class AbstractManagerFragment extends Fragment implements OnCheckedChangeListener, OnClickListener, OnItemSelectedListener, OnItemClickListener {

	// /**
	// * Main activity
	// */
	// protected MainActivity mainActivity;

	/**
	 * File manager
	 */
	protected FileManager localFileManager;

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

		// Get main activity
		// mainActivity = (MainActivity) getActivity();

		// Inflate browser view
		View view = inflater.inflate(R.layout.fragment_browsers, container, false);

		// Initialize user interface
		initWideBrowserUI(view);
		initSmallBrowserUI(view);
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
		// handle select all files event

	}

	/**
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// onWideBrowserGoParentButtonClick();
		// onSmallBrowserGoParentButtonClick();
	}

	/**
	 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView,
	 *      android.view.View, int, long)
	 */
	@Override
	public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		// onUpdateWideBrowserOrderBy(position);
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
		// onWideBrowserListItemClick(position);
		// onSmallBrowserListItemClick(position);
	}
}
