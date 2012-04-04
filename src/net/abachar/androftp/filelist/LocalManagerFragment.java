package net.abachar.androftp.filelist;

import net.abachar.androftp.MainActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * @author abachar
 */
public class LocalManagerFragment extends AbstractManagerFragment {

	/**
	 * Setup fragment view
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// Init file managers
		wideBrowserFileManager = ((MainActivity) getActivity()).getLocalFileManager();
		smallBrowserFileManager = ((MainActivity) getActivity()).getServerFileManager();

		// Create view
		return super.onCreateView(inflater, container, savedInstanceState);
	}
}
