package net.abachar.androftp.filelist;

import java.util.List;

import net.abachar.androftp.filelist.manager.FileEntry;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * 
 * @author abachar
 */
public abstract class AbstractFileAdapter extends BaseAdapter implements ListAdapter {

	/** Layout inflater service */
	protected LayoutInflater mInflater;

	/** List of visible files and directories */
	protected List<FileEntry> mFileList;

	/**
	 *
	 */
	public AbstractFileAdapter(Context context) {

		// Inflater
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// Build list of files
		mFileList = null;
	}

	/**
	 * 
	 */
	public void setFiles(List<FileEntry> files) {
		if ((files != null) && !files.isEmpty()) {
			mFileList = files;
		} else {
			mFileList = null;
		}
		notifyDataSetChanged();
	}

	/**
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return (mFileList != null) ? mFileList.size() : 0;
	}

	/**
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return mFileList.get(position);
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
	protected class ViewHolder {
		CheckBox mCheckbox;
		ImageView mIcon;
		TextView mName;
		TextView mSize;
		TextView mLastModified;
	}
}
