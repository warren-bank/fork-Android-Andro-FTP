package net.abachar.androftp.ui.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.abachar.androftp.R;
import net.abachar.androftp.filelist.FileEntry;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author abachar
 */
public class WideFileAdapter extends AbstractFileAdapter {

	/**
	 *
	 */
	public WideFileAdapter(Context context) {
		super(context);
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
			convertView = inflater.inflate(R.layout.list_item_wide_browser, parent, false);
			holder.checkbox = (CheckBox) convertView.findViewById(R.id.chk_file);
			holder.icon = (ImageView) convertView.findViewById(R.id.file_icon);
			holder.name = (TextView) convertView.findViewById(R.id.file_name);
			holder.size = (TextView) convertView.findViewById(R.id.file_size);
			holder.lastModified = (TextView) convertView.findViewById(R.id.file_last_modified);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		FileEntry file = files.get(position);
		holder.checkbox.setChecked(checkStates.get(position));
		holder.icon.setImageDrawable(file.getType().getIcon());
		holder.name.setText(file.getName());
		holder.lastModified.setText(new SimpleDateFormat("dd/MM/yyyy hh:mm").format(new Date(file.getLastModified())));

		// File size
		if (!file.isFolder()) {
			long size = file.getSize();
			if (size < 1024) {
				holder.size.setText(size + " o");
			} else {
				size = (size / 1024) + 1;
				if (size > 1024) {
					holder.size.setText((size / 1024) + " Mo");
				} else {
					holder.size.setText(size + " Ko");
				}
			}
		} else {
			holder.size.setText(" ");
		}

		return convertView;
	}
}
