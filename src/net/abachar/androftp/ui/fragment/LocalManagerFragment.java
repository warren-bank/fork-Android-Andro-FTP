package net.abachar.androftp.ui.fragment;

import net.abachar.androftp.ui.MainActivity;
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
