package net.abachar.androftp.transfers;

import net.abachar.androftp.MainApplication;
import net.abachar.androftp.R;
import net.abachar.androftp.transfers.manager.TransferListener;
import net.abachar.androftp.transfers.manager.TransferManager;
import android.app.Fragment;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;

/**
 * 
 * @author abachar
 */
public class TransferFragment extends Fragment implements OnClickListener, TransferListener {

	/**  */
	private ActionMode mActionMode;

	/** Managers */
	protected TransferManager mTransferManager;

	/** Window items */
	private CheckBox mSelectAllCheckBox;
	private ListView mTransferListView;
	private TransferAdapter mTransferAdapter;

	/**
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater,
	 *      android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// Inflate browser view
		View view = inflater.inflate(R.layout.fragment_transfers, container, false);

		// Initialize user interface
		initTransferUI(view);

		// Transfer manager
		mTransferManager = MainApplication.getInstance().getTransferManager();
		mTransferManager.setListener(this);

		// Return created view
		return view;
	}

	/**
	 * Setup transfer
	 */
	private void initTransferUI(View view) {

		// Setup check all button
		mSelectAllCheckBox = (CheckBox) view.findViewById(R.id.select_all);
		mSelectAllCheckBox.setOnClickListener(this);

		// Setup transfers list
		mTransferListView = (ListView) view.findViewById(R.id.transfers);

		// Transfer adapter
		mTransferAdapter = new TransferAdapter(getActivity(), this);
		mTransferListView.setAdapter(mTransferAdapter);
	}

	/**
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View view) {

		// List count and nbr of selected items
		int count = mTransferAdapter.getCount();
		int selectedCount;

		if (view.getId() == R.id.select_all) {
			/* Event trigged from select all */
			boolean checked = mSelectAllCheckBox.isChecked();
			mTransferAdapter.updateSelectAllTransfers(checked);
			selectedCount = checked ? count : 0;
		} else {
			selectedCount = mTransferAdapter.getSelectedCount();
			mSelectAllCheckBox.setChecked(selectedCount == count);
		}

		// Manage action mode mode
		if (selectedCount > 0) {
			if (mActionMode == null) {
				mActionMode = getActivity().startActionMode(mSelectAction);
			}

			if (selectedCount == 1) {
				mActionMode.setTitle("1 element selected");
			} else {
				mActionMode.setTitle(selectedCount + " elements selected");
			}
		} else {
			if (mActionMode != null) {
				mActionMode.finish();
			}
		}
	}

	/**
	 * @see net.abachar.androftp.transfers.manager.TransferListener#onUpdateTransfer()
	 */
	@Override
	public void onUpdateTransfer() {
		mTransferAdapter.setTransferList(mTransferManager.getTransferList());
	}

	/**
	 * 
	 */
	private ActionMode.Callback mSelectAction = new ActionMode.Callback() {

		/**
		 * @see android.view.ActionMode.Callback#onCreateActionMode(android.view.ActionMode,
		 *      android.view.Menu)
		 */
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {

			// Menu
			menu.add(R.string.menu_cancel).setIcon(R.drawable.ic_action_cancel).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			return true;
		}

		/**
		 * @see android.view.ActionMode.Callback#onPrepareActionMode(android.view.ActionMode,
		 *      android.view.Menu)
		 */
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		/**
		 * @see android.view.ActionMode.Callback#onActionItemClicked(android.view.ActionMode,
		 *      android.view.MenuItem)
		 */
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

			// Only on action
			mTransferAdapter.removeAllSelected();

			// Uncheck select all checkbox
			mSelectAllCheckBox.setChecked(false);
			mActionMode.finish();
			return true;
		}

		/**
		 * @see android.view.ActionMode.Callback#onDestroyActionMode(android.view.ActionMode)
		 */
		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mActionMode = null;
			mSelectAllCheckBox.setChecked(false);
			mTransferAdapter.updateSelectAllTransfers(false);
		}
	};
}