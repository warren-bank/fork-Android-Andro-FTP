package net.abachar.androftp.filelist;

import net.abachar.androftp.R;
import net.abachar.androftp.filelist.manager.FileEntry;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author abachar
 */
public class SmallFileAdapter extends AbstractFileAdapter {

	/**
	 *
	 */
	public SmallFileAdapter(Context context) {
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
			convertView = mInflater.inflate(R.layout.list_item_small_browser, parent, false);
			holder.mIcon = (ImageView) convertView.findViewById(R.id.file_icon);
			holder.mName = (TextView) convertView.findViewById(R.id.file_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		FileEntry file = mFileList.get(position);
		holder.mIcon.setImageDrawable(file.getType().getIcon());
		holder.mName.setText(file.getName());

		return convertView;
	}
}
