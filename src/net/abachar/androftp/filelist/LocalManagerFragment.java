package net.abachar.androftp.filelist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.abachar.androftp.MainActivity;
import net.abachar.androftp.R;
import net.abachar.androftp.filelist.manager.FileEntry;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * 
 * @author abachar
 */
public class LocalManagerFragment extends AbstractManagerFragment {

	/** */
	private final static int ID_MENU_UPLOAD = 0x01;
	private final static int ID_MENU_DELETE = 0x02;
	private final static int ID_MENU_RENAME = 0x03;
	private final static int ID_MENU_DETAIL = 0x04;

	/** */
	private MenuItem mUploadMenu;
	private MenuItem mDeleteMenu;
	private MenuItem mRenameMenu;
	private MenuItem mDetailMenu;

	/**
	 * Default constructor.
	 */
	public LocalManagerFragment() {
		super();

		// Local layout
		mFragmentLayoutId = R.layout.fragment_local_browsers;
	}

	/**
	 * Setup fragment view
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// Init file managers
		mWideBrowserFileManager = ((MainActivity) getActivity()).getLocalFileManager();
		mSmallBrowserFileManager = ((MainActivity) getActivity()).getServerFileManager();

		// Create view
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	/**
	 * 
	 */
	protected boolean _onCreateActionMode(Menu menu) {

		// Menu upload
		mUploadMenu = menu.add(Menu.NONE, ID_MENU_UPLOAD, Menu.NONE, R.string.menu_upload);
		mUploadMenu.setIcon(R.drawable.ic_action_upload);
		mUploadMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

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
	 */
	protected boolean _onPrepareActionMode(Menu menu) {

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
	protected boolean _onActionItemClicked(MenuItem item) {

		switch (item.getItemId()) {

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
	 * 
	 */
	protected void onMenuDelete() {

		final List<FileEntry> selectedFiles = mWideBrowserFileAdapter.getSelectedFiles();
		int count = selectedFiles.size();

		// Ask yes ?
		AlertDialog.Builder prompt = new AlertDialog.Builder(getActivity());
		prompt.setTitle("Delete");
		if (count == 1) {
			prompt.setMessage("This item is removed");
		} else {
			prompt.setMessage(count + " items will be removed");
		}

		prompt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				mWideBrowserFileManager.deleteFiles(selectedFiles);
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
}
