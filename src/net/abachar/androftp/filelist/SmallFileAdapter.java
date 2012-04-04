package net.abachar.androftp.filelist;

import net.abachar.androftp.R;
import net.abachar.androftp.filelist.manager.FileEntry;
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
	public SmallFileAdapter(AbstractManagerFragment amf) {
		super(amf);
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
			convertView = inflater.inflate(R.layout.list_item_small_browser, parent, false);
			holder.icon = (ImageView) convertView.findViewById(R.id.file_icon);
			holder.name = (TextView) convertView.findViewById(R.id.file_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		FileEntry file = files.get(position);
		holder.icon.setImageDrawable(file.getType().getIcon());
		holder.name.setText(file.getName());

		return convertView;
	}
}
