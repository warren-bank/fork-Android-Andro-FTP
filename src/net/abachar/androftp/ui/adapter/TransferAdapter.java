package net.abachar.androftp.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import net.abachar.androftp.R;
import net.abachar.androftp.transfers.Transfer;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 
 * @author abachar
 */
public class TransferAdapter extends BaseAdapter implements ListAdapter {

	/** Layout inflater service */
	private LayoutInflater inflater;

	/** List of all active transfers */
	private List<Transfer> transfers;

	/** Icons */
	private Drawable uploadDirectionIcon;
	private Drawable downloadDirectionIcon;

	/**
	 *
	 */
	public TransferAdapter(Context context) {

		// Inflater
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// Setup icons
		uploadDirectionIcon = context.getResources().getDrawable(R.drawable.ic_transfer_upload);
		downloadDirectionIcon = context.getResources().getDrawable(R.drawable.ic_transfer_download);

		// Setup list transfers
		transfers = new ArrayList<Transfer>();
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
			convertView = inflater.inflate(R.layout.list_item_transfer, parent, false);
			holder.checkbox = (CheckBox) convertView.findViewById(R.id.transfer_checkbox);
			holder.checkbox.setTag(new Integer(position));
			// holder.checkbox.setOnClickListener(fragment);
			holder.directionIcon = (ImageView) convertView.findViewById(R.id.transfer_direction_icon);
			holder.sourcePath = (TextView) convertView.findViewById(R.id.transfer_source_path);
			holder.destinationPath = (TextView) convertView.findViewById(R.id.transfer_destination_path);
			holder.fileSize = (TextView) convertView.findViewById(R.id.transfer_file_size);
			holder.progress = (ProgressBar) convertView.findViewById(R.id.transfer_progress);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Transfer transfer = transfers.get(position);
		holder.checkbox.setChecked(transfer.isChecked());
		holder.sourcePath.setText(transfer.getSourcePath());
		holder.destinationPath.setText(transfer.getDestinationPath());
		holder.fileSize.setText(transfer.getStringFileSize());
		holder.progress.setProgress(transfer.getProgress());

		if (transfer.isUpload()) {
			holder.directionIcon.setImageDrawable(uploadDirectionIcon);
		} else {
			holder.directionIcon.setImageDrawable(downloadDirectionIcon);
		}

		return convertView;
	}

	/**
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return transfers.size();
	}

	/**
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return transfers.get(position);
	}

	/**
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 
	 * @param tag
	 * @param checked
	 */
	public void checkAtPosition(Integer position, boolean checked) {
		transfers.get(position).setChecked(checked);
	}

//	/**
//	 * 
//	 */
//	public void checkAll(boolean isChecked) {
//		for (TransferInfo transfer : transfers) {
//			transfer.setChecked(isChecked);
//		}
//
//		// Refresh view
//		notifyDataSetChanged();
//	}

	// /**
	// * -1 : No selection 0 : There is a selection but not all lines are
	// selected
	// * 1 : All lines are selected
	// */
	// public int getCheckState() {
	// int count = 0;
	// for (TransferInfo transfer : transfers) {
	// if (transfer.isChecked()) {
	// count++;
	// }
	// }
	//
	// // No selection
	// if (count == 0) {
	// return -1;
	// }
	//
	// // All lines are selected
	// if (count == transfers.size()) {
	// return 1;
	// }
	//
	// // Mixte
	// return 0;
	// }

	/**
	 * List item view holder
	 */
	private class ViewHolder {
		CheckBox checkbox;
		ImageView directionIcon;
		TextView sourcePath;
		TextView destinationPath;
		TextView fileSize;
		ProgressBar progress;
	}
}
