package net.abachar.androftp.filelist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.abachar.androftp.R;
import net.abachar.androftp.filelist.manager.FileEntry;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author abachar
 */
public class WideFileAdapter extends AbstractFileAdapter implements OnClickListener {

	/** Checkboxs */
	private OnClickListener mOnCheckBoxClickListener;

	/**
	 *
	 */
	public WideFileAdapter(Context context, OnClickListener onCheckBoxClickListener) {
		super(context);
		mOnCheckBoxClickListener = onCheckBoxClickListener;
	}

	/**
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 *      android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.list_item_wide_browser, parent, false);
			holder.mCheckbox = (CheckBox) convertView.findViewById(R.id.chk_file);
			holder.mCheckbox.setOnClickListener(this);
			holder.mIcon = (ImageView) convertView.findViewById(R.id.file_icon);
			holder.mName = (TextView) convertView.findViewById(R.id.file_name);
			holder.mSize = (TextView) convertView.findViewById(R.id.file_size);
			holder.mLastModified = (TextView) convertView.findViewById(R.id.file_last_modified);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		FileEntry file = mFileList.get(position);
		holder.mCheckbox.setChecked(file.isChecked());
		holder.mCheckbox.setTag(new Integer(position));
		holder.mIcon.setImageDrawable(file.getType().getIcon());
		holder.mName.setText(file.getName());
		holder.mLastModified.setText(new SimpleDateFormat("dd/MM/yyyy hh:mm").format(new Date(file.getLastModified())));

		// File size
		if (!file.isFolder()) {
			long size = file.getSize();
			if (size < 1024) {
				holder.mSize.setText(size + " o");
			} else {
				size = (size / 1024) + 1;
				if (size > 1024) {
					holder.mSize.setText((size / 1024) + " Mo");
				} else {
					holder.mSize.setText(size + " Ko");
				}
			}
		} else {
			holder.mSize.setText(" ");
		}

		return convertView;
	}

	/**
	 * 
	 * @param isChecked
	 */
	public void updateSelectAllFiles(boolean checked) {
		if (mFileList != null) {
			for (FileEntry file : mFileList) {
				file.setChecked(checked);
			}

			// Refresh view
			notifyDataSetChanged();
		}
	}

	/**
	 * 
	 * @return
	 */
	public List<FileEntry> getSelectedFiles() {
		List<FileEntry> list = null;
		if (mFileList != null) {
			list = new ArrayList<FileEntry>();
			for (FileEntry file : mFileList) {
				if (file.isChecked()) {
					list.add(file);
				}
			}
		}

		return list;
	}

	/**
	 * 
	 * @return
	 */
	public FileEntry getFirstSelectedFile() {
		if (mFileList != null) {
			for (FileEntry file : mFileList) {
				if (file.isChecked()) {
					return file;
				}
			}
		}

		return null;
	}

	/**
	 * 
	 * @return
	 */
	public int getSelectedCount() {
		int count = 0;
		if (mFileList != null) {
			for (FileEntry file : mFileList) {
				if (file.isChecked()) {
					count++;
				}
			}
		}

		return count;
	}

	/**
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {

		// Save state
		Integer tag = (Integer) v.getTag();
		FileEntry fe = mFileList.get(tag.intValue());
		fe.setChecked(((CheckBox) v).isChecked());

		// Notify listener
		mOnCheckBoxClickListener.onClick(v);
	}
}
