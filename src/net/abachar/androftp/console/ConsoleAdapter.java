package net.abachar.androftp.console;

import java.util.ArrayList;
import java.util.List;

import net.abachar.androftp.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * 
 * @author abachar
 */
public class ConsoleAdapter extends BaseAdapter implements ListAdapter {

	/** Layout inflater service */
	private LayoutInflater inflater;

	/** List of all logs */
	private List<ConsoleEntry> entries;

	/**
	 *
	 */
	public ConsoleAdapter(Context context) {

		// Inflater
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// Setup list logs
		entries = new ArrayList<ConsoleEntry>();
	}

	/**
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 *      android.view.ViewGroup)
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_item_console, parent, false);
			holder.message = (TextView) convertView.findViewById(R.id.console_message);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ConsoleEntry entry = entries.get(position);
		holder.message.setText(entry.getMessage());
		return convertView;
	}

	/**
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return entries.size();
	}

	/**
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return entries.get(position);
	}

	/**
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * List item view holder
	 */
	private class ViewHolder {
		TextView message;
	}
}
