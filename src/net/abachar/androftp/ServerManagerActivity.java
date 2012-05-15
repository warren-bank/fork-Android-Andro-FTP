package net.abachar.androftp;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.abachar.androftp.servers.Logontype;
import net.abachar.androftp.servers.Server;
import net.abachar.androftp.servers.ServerDataSource;
import net.abachar.androftp.util.FileType;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author abachar
 */
public class ServerManagerActivity extends Activity implements OnClickListener, OnItemSelectedListener {

	/** Server list */
	private ServerDataSource mDataSource;
	private List<Server> mServerList;
	private ArrayAdapter<String> mServerAdapter;

	/** Window components */
	private Spinner mServerSpinner;
	private TextView mServerNameText;
	private TextView mServerHostText;
	private TextView mServerPortText;
	private TextView mServerUsernameText;
	private TextView mServerPasswordText;
	private RadioButton mLogonTypeAnonymousButton;
	private RadioButton mLogonTypeNormalButton;
	private Button mConnectButton;
	private Button mNewServerButton;
	private Button mDeleteButton;
	private Button mSaveButton;
	private Button mCancelButton;

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create file types map
		loadFileTypes();

		// Use server_manager view
		setContentView(R.layout.server_manager);

		// Initialize user interface
		initUI();

		// Load servers
		mDataSource = new ServerDataSource(this);
		mServerList = mDataSource.getAllServers();

		// Setup servers spinner
		mServerSpinner = (Spinner) findViewById(R.id.spn_servers);
		mServerSpinner.setOnItemSelectedListener(this);

		// Setup servers spinner adapter
		mServerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		mServerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mServerSpinner.setAdapter(mServerAdapter);

		// Get last used server
		fillServerAdapter(MainApplication.getInstance().getLastSelectedServer());
	}

	/**
	 * 
	 */
	private void loadFileTypes() {

		try {
			XmlResourceParser xrp = getResources().getXml(R.xml.file_types);
			xrp.next();
			int eventType = xrp.getEventType();
			int order = 1; // 0 is reserved to folder
			String tag, text = null, code = null, ext = null, icon = null, format = null, ascii = null;

			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {

					case XmlPullParser.START_TAG:
						if ("file-type".equals(xrp.getName())) {
							code = null;
							ext = null;
							icon = null;
							format = null;
							ascii = null;
						}
						break;

					case XmlPullParser.END_TAG:
						tag = xrp.getName();

						if ("code".equals(tag)) {
							code = text;
						} else if ("extension".equals(tag)) {
							ext = text;
						} else if ("icon".equals(tag)) {
							icon = text;
						} else if ("format".equals(tag)) {
							format = text;
						} else if ("ascii".equals(tag)) {
							ascii = text;
						} else if ("file-type".equals(tag)) {
							// Add current file type
							FileType.addFileType(code, order++, ext, icon, format, ascii);
						}
						break;

					case XmlPullParser.TEXT:
						text = xrp.getText();
						break;
				}
				eventType = xrp.next();
			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public void initUI() {

		// Setup edit account form
		mServerNameText = (TextView) findViewById(R.id.txt_server_name);
		mServerHostText = (TextView) findViewById(R.id.txt_server_host);
		mServerPortText = (TextView) findViewById(R.id.txt_server_port);
		mServerUsernameText = (TextView) findViewById(R.id.txt_server_username);
		mServerPasswordText = (TextView) findViewById(R.id.txt_server_password);
		mLogonTypeAnonymousButton = (RadioButton) findViewById(R.id.rdo_logontype_anonymous);
		mLogonTypeNormalButton = (RadioButton) findViewById(R.id.rdo_logontype_normal);
		mConnectButton = (Button) findViewById(R.id.btn_connect);
		mNewServerButton = (Button) findViewById(R.id.btn_newserver);
		mDeleteButton = (Button) findViewById(R.id.btn_delete);
		mSaveButton = (Button) findViewById(R.id.btn_save);
		mCancelButton = (Button) findViewById(R.id.btn_cancel);

		// Button OnClickListener
		mConnectButton.setOnClickListener(this);
		mNewServerButton.setOnClickListener(this);
		mDeleteButton.setOnClickListener(this);
		mSaveButton.setOnClickListener(this);
		mCancelButton.setOnClickListener(this);
		mLogonTypeAnonymousButton.setOnClickListener(this);
		mLogonTypeNormalButton.setOnClickListener(this);
	}

	/**
	 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView,
	 *      android.view.View, int, long)
	 */
	@Override
	public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		if (position == 0) {
			onNewServerSelected();
		} else {
			mConnectButton.setEnabled(true);
			mNewServerButton.setEnabled(true);
			mDeleteButton.setEnabled(true);
			fillServerDetails(mServerList.get(position - 1));
		}
	}

	/**
	 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android.widget.AdapterView)
	 */
	@Override
	public void onNothingSelected(AdapterView<?> parentView) {
	}

	/**
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_connect:
				onConnect();
				break;

			case R.id.btn_newserver:
				onNewServer();
				break;

			case R.id.btn_delete:
				onDelete();
				break;

			case R.id.btn_save:
				onSave();
				break;

			case R.id.btn_cancel:
				onCancel();
				break;

			case R.id.rdo_logontype_anonymous:
			case R.id.rdo_logontype_normal:
				onChangeLogontype(v.getId() == R.id.rdo_logontype_anonymous);
				break;
		}
	}

	/**
	 * 
	 */
	private void onConnect() {
		// Create main activity
		Intent intent = new Intent(this, MainActivity.class);

		// Server data
		Server server = mServerList.get(mServerSpinner.getSelectedItemPosition() - 1);

		// Save
		MainApplication.getInstance().saveLastSelectedServer(server.getId());

		// Start activity
		intent.putExtra("host", server.getHost());
		intent.putExtra("port", server.getPort());
		intent.putExtra("logontype", server.getLogontype());
		if (server.getLogontype() == Logontype.NORMAL) {
			intent.putExtra("username", server.getUsername());
			intent.putExtra("password", server.getPassword());
		}
		startActivity(intent);
	}

	/**
	 * 
	 */
	private void onNewServer() {
		mServerSpinner.setSelection(0);
		onNewServerSelected();
	}

	/**
	 *
	 */
	private void onNewServerSelected() {
		mConnectButton.setEnabled(false);
		mNewServerButton.setEnabled(false);
		mDeleteButton.setEnabled(false);
		fillServerDetails(null);
	}

	/**
	 * 
	 */
	private void onDelete() {
		Server server = mServerList.get(mServerSpinner.getSelectedItemPosition() - 1);
		if (mDataSource.deleteServer(server.getId())) {
			mServerList.remove(server);
			mServerSpinner.setSelection(0);
			mServerAdapter.remove(server.getName());
		} else {
			Toast.makeText(this, "Error : can't delete site '" + server.getName() + "' :(", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 
	 */
	private void onSave() {
		Server server;

		/**
		 * 
		 * @author abachar
		 */
		class ValidationException extends Exception {
			/** serialVersionUID */
			private static final long serialVersionUID = 1L;
			/** Id string error */
			int messageId;

			/** */
			public ValidationException(int messageId) {
				this.messageId = messageId;
			}
		}

		try {
			// Id
			int selectedIndex = mServerSpinner.getSelectedItemPosition();
			if (selectedIndex == 0) {
				server = new Server();
				server.setId(-1);
			} else {
				server = mServerList.get(selectedIndex - 1);
			}

			// Server name
			String val = mServerNameText.getText().toString().trim();
			if (val.isEmpty()) {
				throw new ValidationException(R.string.server_manager_error_name_required);
			}
			server.setName(val);

			// Server host
			val = mServerHostText.getText().toString().trim();
			if (val.isEmpty()) {
				throw new ValidationException(R.string.server_manager_error_host_required);
			}
			server.setHost(val);

			// Server port
			val = mServerPortText.getText().toString().trim();
			if (!val.isEmpty()) {
				server.setPort(Integer.valueOf(val));
			} else {
				server.setPort(21);
			}

			// Logon type
			if (mLogonTypeAnonymousButton.isChecked()) {
				server.setLogontype(Logontype.ANONYMOUS);
				server.setUsername(null);
				server.setPassword(null);
			} else {
				server.setLogontype(Logontype.NORMAL);

				// Server username
				val = mServerUsernameText.getText().toString().trim();
				if (val.isEmpty()) {
					throw new ValidationException(R.string.server_manager_error_username_required);
				}
				server.setUsername(val);

				// Server password
				val = mServerPasswordText.getText().toString().trim();
				if (val.isEmpty()) {
					throw new ValidationException(R.string.server_manager_error_password_required);
				}
				server.setPassword(val);
			}

			// Save to database
			mDataSource.saveServer(server);
			if (selectedIndex == 0) {
				mServerList.add(server);
			}

			// Sort the new list
			Collections.sort(mServerList, new Comparator<Server>() {
				@Override
				public int compare(Server lhs, Server rhs) {
					return lhs.getName().toLowerCase().compareTo(rhs.getName().toLowerCase());
				}
			});

			// Update adapter
			fillServerAdapter(server.getId());

		} catch (ValidationException ex) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(ex.messageId));
			builder.setCancelable(true);
			builder.setNeutralButton("Close", null);
			builder.create().show();
		}
	}

	/**
	 * 
	 */
	private void onCancel() {
		int selectedIndex = mServerSpinner.getSelectedItemPosition();
		if (selectedIndex == 0) {
			fillServerDetails(null);
		} else {
			fillServerDetails(mServerList.get(selectedIndex - 1));
		}
	}

	/**
	 * 
	 * @param logontype
	 */
	private void onChangeLogontype(boolean isAnonymous) {
		mServerUsernameText.setText("");
		mServerPasswordText.setText("");
		mServerUsernameText.setEnabled(!isAnonymous);
		mServerPasswordText.setEnabled(!isAnonymous);
	}

	/**
	 * Populate site detail
	 */
	private void fillServerDetails(Server server) {

		if (server == null) {
			mServerNameText.setText(null);
			mServerHostText.setText(null);
			mServerPortText.setText(null);
			mServerUsernameText.setText(null);
			mServerPasswordText.setText(null);

			mLogonTypeAnonymousButton.setChecked(false);
			mLogonTypeNormalButton.setChecked(true);
			mServerUsernameText.setEnabled(true);
			mServerPasswordText.setEnabled(true);
		} else {
			mServerNameText.setText(server.getName());
			mServerHostText.setText(server.getHost());
			mServerPortText.setText(Integer.toString(server.getPort()));

			if (server.getLogontype() == Logontype.ANONYMOUS) {
				mLogonTypeAnonymousButton.setChecked(true);
				mLogonTypeNormalButton.setChecked(false);
				mServerUsernameText.setText("");
				mServerPasswordText.setText("");
				mServerUsernameText.setEnabled(false);
				mServerPasswordText.setEnabled(false);
			} else {
				mLogonTypeAnonymousButton.setChecked(false);
				mLogonTypeNormalButton.setChecked(true);
				mServerUsernameText.setText(server.getUsername());
				mServerPasswordText.setText(server.getPassword());
				mServerUsernameText.setEnabled(true);
				mServerPasswordText.setEnabled(true);
			}
		}
	}

	/**
	 * 
	 * @param id
	 */
	private void fillServerAdapter(long toSelectId) {

		// Clear list
		if (!mServerAdapter.isEmpty()) {
			mServerAdapter.clear();
		}

		// Add new server entry
		mServerAdapter.add(getString(R.string.server_manager_label_new_server));

		// Add servers items
		int index = 0, selectedIndex = 0;
		for (Server server : mServerList) {
			mServerAdapter.add(server.getName());

			if (toSelectId == server.getId()) {
				selectedIndex = index + 1;
			}
			index++;
		}

		mServerSpinner.setSelection(selectedIndex);
	}
}
