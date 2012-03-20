package net.abachar.androftp.ui.fragment;

import net.abachar.androftp.R;
import net.abachar.androftp.ui.adapter.TransferAdapter;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;

/**
 * 
 * @author abachar
 */
public class TransferFragment extends Fragment implements OnClickListener {

	/**
	 * Wide browser
	 */
	private CheckBox chkSelectAll;
	private ListView lsvTransfers;
	private TransferAdapter transferAdapter;

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

		// Return created view
		return view;
	}
	
	/**
	 * Setup transfer
	 */
	private void initTransferUI(View view) {

		// Setup check all button
		chkSelectAll = (CheckBox) view.findViewById(R.id.select_all);
		chkSelectAll.setOnClickListener(this);

		// Setup transfers list
		lsvTransfers = (ListView) view.findViewById(R.id.transfers);

		// Transfer adapter
		transferAdapter = new TransferAdapter(getActivity());
		lsvTransfers.setAdapter(transferAdapter);
	}

	/**
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
	}
}