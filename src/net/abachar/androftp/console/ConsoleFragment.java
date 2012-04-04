package net.abachar.androftp.console;

import net.abachar.androftp.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
	 * Menus
	 */
	protected MenuItem mClearMenu;

	/**
	 * @see android.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

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

	/**
	 * @see android.app.Fragment#onCreateOptionsMenu(android.view.Menu,
	 *      android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		// Clear
		mClearMenu = menu.add(R.string.menu_clear);
		mClearMenu.setIcon(R.drawable.ic_action_cancel);
		mClearMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	}
}