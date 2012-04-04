package net.abachar.androftp.filelist;

import java.util.Collections;
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
	protected LayoutInflater inflater;

	/** List of visible files and directories */
	protected List<FileEntry> files;
	protected List<Boolean> checkStates;

	/**
	 *
	 */
	public AbstractFileAdapter(AbstractManagerFragment amf) {

		// Inflater
		inflater = (LayoutInflater) amf.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// Build list of files
		this.files = null;
		this.checkStates = null;
	}

	/**
	 * 
	 */
	public void setFiles(List<FileEntry> files) {
		this.files = files;
		if (files != null) {
			this.checkStates = Collections.nCopies(files.size(), Boolean.FALSE);
		} else {
			this.checkStates = null;
		}
		notifyDataSetChanged();
	}

	/**
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return (files != null) ? files.size() : 0;
	}

	/**
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return files.get(position);
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
	 * @param isChecked
	 */
	public void selectAllFiles(boolean isChecked) {
		this.checkStates = Collections.nCopies(files.size(), new Boolean(isChecked));
		notifyDataSetChanged();
	}

	/**
	 * List item view holder
	 */
	protected class ViewHolder {
		CheckBox checkbox;
		ImageView icon;
		TextView name;
		TextView size;
		TextView lastModified;
	}
}
