package net.abachar.androftp.ui.fragment;

import net.abachar.androftp.R;
import net.abachar.androftp.ui.adapter.ConsoleAdapter;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * 
 * @author abachar
 */
public class ConsoleFragment extends Fragment {

	/**
	 * Wide browser
	 */
	private ListView lsvConsole;
	private ConsoleAdapter consoleAdapter;

	/**
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater,
	 *      android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// Inflate browser view
		View view = inflater.inflate(R.layout.fragment_console, container, false);

		// Initialize user interface
		initConsoleUI(view);

		// Return created view
		return view;
	}
	
	/**
	 * Setup transfer
	 */
	private void initConsoleUI(View view) {

		// Setup transfers list
		lsvConsole = (ListView) view.findViewById(R.id.console);

		// Transfer adapter
		consoleAdapter = new ConsoleAdapter(getActivity());
		lsvConsole.setAdapter(consoleAdapter);
	}
}