package net.abachar.androftp.transfers;

import java.util.ArrayList;
import java.util.List;

import net.abachar.androftp.R;
import net.abachar.androftp.transfers.manager.Transfer;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
public class TransferAdapter extends BaseAdapter implements ListAdapter, OnClickListener {

	/** */
	private OnClickListener mOnCheckBoxClickListener;

	/** Layout inflater service */
	private LayoutInflater mInflater;

	/** List of all active transfers */
	private List<Transfer> mTransferList;

	/** Icons */
	private Drawable mUploadIcon;
	private Drawable mDownloadIcon;

	/**
	 *
	 */
	public TransferAdapter(Context context, OnClickListener onCheckBoxClickListener) {

		// Inflater
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// On click listener
		mOnCheckBoxClickListener = onCheckBoxClickListener;

		// Setup icons
		mUploadIcon = context.getResources().getDrawable(R.drawable.ic_transfer_upload);
		mDownloadIcon = context.getResources().getDrawable(R.drawable.ic_transfer_download);

		// Setup list transfers
		mTransferList = null;
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
			convertView = mInflater.inflate(R.layout.list_item_transfer, parent, false);
			holder.mCheckbox = (CheckBox) convertView.findViewById(R.id.transfer_checkbox);
			holder.mCheckbox.setOnClickListener(this);
			holder.mDirection = (ImageView) convertView.findViewById(R.id.transfer_direction_icon);
			holder.mSourcePath = (TextView) convertView.findViewById(R.id.transfer_source_path);
			holder.mDestinationPath = (TextView) convertView.findViewById(R.id.transfer_destination_path);
			holder.mFileSize = (TextView) convertView.findViewById(R.id.transfer_file_size);
			holder.mProgress = (ProgressBar) convertView.findViewById(R.id.transfer_progress);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Transfer transfer = mTransferList.get(position);
		holder.mCheckbox.setChecked(transfer.isChecked());
		holder.mCheckbox.setTag(new Integer(position));
		holder.mSourcePath.setText(transfer.getSourcePath());
		holder.mDestinationPath.setText(transfer.getDestinationPath());
		holder.mProgress.setProgress(transfer.getProgress());

		if (transfer.isUpload()) {
			holder.mDirection.setImageDrawable(mUploadIcon);
		} else {
			holder.mDirection.setImageDrawable(mDownloadIcon);
		}

		// File size
		long size = transfer.getFileSize();
		if (size < 1024) {
			holder.mFileSize.setText(size + " o");
		} else {
			size = (size / 1024) + 1;
			if (size > 1024) {
				holder.mFileSize.setText((size / 1024) + " Mo");
			} else {
				holder.mFileSize.setText(size + " Ko");
			}
		}

		return convertView;
	}

	/**
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return (mTransferList != null) ? mTransferList.size() : 0;
	}

	/**
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return mTransferList.get(position);
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
	 * @param selectAllChecked
	 */
	public void updateSelectAllTransfers(boolean isChecked) {
		if (mTransferList != null) {
			for (Transfer transfer : mTransferList) {
				transfer.setChecked(isChecked);
			}

			// Refresh view
			notifyDataSetChanged();
		}
	}

	/**
	 * 
	 */
	public void removeAllSelected() {

		List<Transfer> oldTransferList = mTransferList;
		mTransferList = new ArrayList<Transfer>();

		// Copy
		for (Transfer transfer : oldTransferList) {
			if (!transfer.isChecked()) {
				mTransferList.add(transfer);
			}
		}
		if (mTransferList.isEmpty()) {
			mTransferList = null;
		}

		// Refresh view
		notifyDataSetChanged();
	}

	/**
	 * 
	 * @return
	 */
	public int getSelectedCount() {
		int count = 0;
		if (mTransferList != null) {
			for (Transfer transfer : mTransferList) {
				if (transfer.isChecked()) {
					count++;
				}
			}
		}

		return count;
	}

	/**
	 * @param mTransferList
	 *            the mTransferList to set
	 */
	public void setTransferList(List<Transfer> transferList) {

		if ((transferList != null) && !transferList.isEmpty()) {
			mTransferList = transferList;
		} else {
			mTransferList = null;
		}
		notifyDataSetChanged();

	}

	/**
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {

		// Save state
		mTransferList.get(((Integer) v.getTag()).intValue()).setChecked(((CheckBox) v).isChecked());

		// Notify listener
		mOnCheckBoxClickListener.onClick(v);
	}

	/**
	 * List item view holder
	 */
	private class ViewHolder {
		CheckBox mCheckbox;
		ImageView mDirection;
		TextView mSourcePath;
		TextView mDestinationPath;
		TextView mFileSize;
		ProgressBar mProgress;
	}
}
