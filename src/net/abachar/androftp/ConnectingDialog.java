package net.abachar.androftp;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * @author abachar
 */
public class ConnectingDialog extends Dialog {

	/** */
	private TextView mConnectingTo;

	/** */
	private ListView mConnectingDetails;
	private ConnectingDetailsAdapter mConnectingDetailsAdapter;

	/**
	 * 
	 * @param context
	 */
	public ConnectingDialog(Context context) {
		super(context);

		// Content view
		setContentView(R.layout.connecting_dialog);

		// Title
		setTitle(R.string.connecting_dialog_title);

		// Connecting to message
		mConnectingTo = (TextView) findViewById(R.id.connecting_to);

		// Console
		mConnectingDetails = (ListView) findViewById(R.id.connecting_details);
		mConnectingDetailsAdapter = new ConnectingDetailsAdapter();
		mConnectingDetails.setAdapter(mConnectingDetailsAdapter);

		// Avoid cancel button
		setCancelable(false);
	}

	/**
	 * @param serverName
	 *            the serverName to set
	 */
	public void setServerName(String serverName) {
		mConnectingTo.setText(getContext().getString(R.string.connecting_dialog_message, serverName));
	}

	/**
	 * 
	 * @param message
	 */
	public void addLog(String message) {

		// Split lines
		String lines[] = message.split("\\r?\\n");
		for (String line : lines) {
			mConnectingDetailsAdapter.logs.add(line);
		}
		mConnectingDetailsAdapter.notifyDataSetChanged();

		mConnectingDetails.smoothScrollToPosition(mConnectingDetailsAdapter.getCount() - 1);
	}

	/**
	 * 
	 * @author abachar
	 */
	private class ConnectingDetailsAdapter extends BaseAdapter implements ListAdapter {

		/** */
		private List<String> logs = new ArrayList<String>();

		/**
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			return logs.size();
		}

		/**
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int position) {
			return logs.get(position);
		}

		/**
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			return position;
		}

		/**
		 * @see android.widget.Adapter#getView(int, android.view.View,
		 *      android.view.ViewGroup)
		 */
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = new TextView(parent.getContext());
			}

			((TextView) convertView).setText(logs.get(position));
			return convertView;
		}
	}
}
