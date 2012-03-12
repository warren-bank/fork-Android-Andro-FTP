package net.abachar.androftp.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import net.abachar.androftp.filelist.FileEntry;
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

	/**
	 * List of visible files and directories
	 */
	protected List<FileEntry> files;

	/** */
	protected LayoutInflater inflater;

	/**
	 *
	 */
	public AbstractFileAdapter(Context context) {
		this(context, new ArrayList<FileEntry>());
	}

	/**
	 *
	 */
	public AbstractFileAdapter(Context context, List<FileEntry> files) {

		// Inflater
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// Build list of files
		this.files = files;
	}

	/**
	 * 
	 */
	public void chanegListFiles(List<FileEntry> files) {
		this.files = files;
		notifyDataSetChanged();
	}

	/**
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return files.size();
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
