package net.abachar.androftp.ui;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.abachar.androftp.R;
import net.abachar.androftp.servers.Logontype;
import net.abachar.androftp.servers.Server;
import net.abachar.androftp.servers.ServerDataSource;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
	
	/** */
	private static final String PREFS_NAME = "AndroFTPPrefsFile";
	private SharedPreferences settings;

	/** Datasource */
	private ServerDataSource ds;
	private List<Server> listServers;
	private ArrayAdapter<String> serverAdapter;

	/** Window components */
	private Spinner spnServers;
	private TextView txtServerName;
	private TextView txtServerHost;
	private TextView txtServerPort;
	private TextView txtServerUsername;
	private TextView txtServerPassword;
	private RadioButton rdoLogonTypeAnonymous;
	private RadioButton rdoLogonTypeNormal;
	private Button btnConnect;
	private Button btnNewServer;
	private Button btnDelete;
	private Button btnSave;
	private Button btnCancel;

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Use server_manager view
		setContentView(R.layout.server_manager);
		
		// Load settings
		settings = getSharedPreferences(PREFS_NAME, 0);

		// Initialize user interface
		initUI();

		// Load accounts
		ds = new ServerDataSource(this);
		listServers = ds.getAllServers();

		// Setup accounts spinner
		spnServers = (Spinner) findViewById(R.id.spn_servers);
		spnServers.setOnItemSelectedListener(this);

		// Setup accounts spinner adapter
		serverAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		serverAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnServers.setAdapter(serverAdapter);
		
		Long lastSelectedServer = settings.getLong("lastSelectedServer", -1);
		fillServerAdapter(lastSelectedServer);
		if (lastSelectedServer != -1) {
			btnConnect.requestFocus();
		}
	}

	/**
	 * 
	 */
	public void initUI() {

		// Setup edit account form
		txtServerName = (TextView) findViewById(R.id.txt_server_name);
		txtServerHost = (TextView) findViewById(R.id.txt_server_host);
		txtServerPort = (TextView) findViewById(R.id.txt_server_port);
		txtServerUsername = (TextView) findViewById(R.id.txt_server_username);
		txtServerPassword = (TextView) findViewById(R.id.txt_server_password);
		rdoLogonTypeAnonymous = (RadioButton) findViewById(R.id.rdo_logontype_anonymous);
		rdoLogonTypeNormal = (RadioButton) findViewById(R.id.rdo_logontype_normal);
		btnConnect = (Button) findViewById(R.id.btn_connect);
		btnNewServer = (Button) findViewById(R.id.btn_newserver);
		btnDelete = (Button) findViewById(R.id.btn_delete);
		btnSave = (Button) findViewById(R.id.btn_save);
		btnCancel = (Button) findViewById(R.id.btn_cancel);

		// Button OnClickListener
		btnConnect.setOnClickListener(this);
		btnNewServer.setOnClickListener(this);
		btnDelete.setOnClickListener(this);
		btnSave.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		rdoLogonTypeAnonymous.setOnClickListener(this);
		rdoLogonTypeNormal.setOnClickListener(this);
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
			btnConnect.setEnabled(true);
			btnNewServer.setEnabled(true);
			btnDelete.setEnabled(true);
			fillServerDetails(listServers.get(position - 1));
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
		Server server = listServers.get(spnServers.getSelectedItemPosition() - 1);
		
		// Save 
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong("lastSelectedServer", server.getId());
		editor.commit();

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
		spnServers.setSelection(0);
		onNewServerSelected();
	}

	/**
	 *
	 */
	private void onNewServerSelected() {
		btnConnect.setEnabled(false);
		btnNewServer.setEnabled(false);
		btnDelete.setEnabled(false);
		fillServerDetails(new Server());
	}

	/**
	 * 
	 */
	private void onDelete() {
		Server server = listServers.get(spnServers.getSelectedItemPosition() - 1);
		if (ds.deleteServer(server.getId())) {
			listServers.remove(server);
			spnServers.setSelection(0);
			serverAdapter.remove(server.getName());
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
			int selectedIndex = spnServers.getSelectedItemPosition();
			if (selectedIndex == 0) {
				server = new Server();
				server.setId(-1);
			} else {
				server = listServers.get(selectedIndex - 1);
			}

			// Server name
			String val = txtServerName.getText().toString().trim();
			if (val.isEmpty()) {
				throw new ValidationException(R.string.server_manager_error_name_required);
			}
			server.setName(val);

			// Server host
			val = txtServerHost.getText().toString().trim();
			if (val.isEmpty()) {
				throw new ValidationException(R.string.server_manager_error_host_required);
			}
			server.setHost(val);

			// Server port
			val = txtServerPort.getText().toString().trim();
			if (val.isEmpty()) {
				throw new ValidationException(R.string.server_manager_error_port_required);
			}
			server.setPort(Integer.valueOf(val));

			// Logon type
			if (rdoLogonTypeAnonymous.isChecked()) {
				server.setLogontype(Logontype.ANONYMOUS);
				server.setUsername(null);
				server.setPassword(null);
			} else {
				server.setLogontype(Logontype.NORMAL);

				// Server username
				val = txtServerUsername.getText().toString().trim();
				if (val.isEmpty()) {
					throw new ValidationException(R.string.server_manager_error_username_required);
				}
				server.setUsername(val);

				// Server password
				val = txtServerPassword.getText().toString().trim();
				if (val.isEmpty()) {
					throw new ValidationException(R.string.server_manager_error_password_required);
				}
				server.setPassword(val);
			}

			// Save to database
			ds.saveServer(server);
			if (selectedIndex == 0) {
				listServers.add(server);
			}

			// Sort the new list
			Collections.sort(listServers, new Comparator<Server>() {
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
		int selectedIndex = spnServers.getSelectedItemPosition();
		if (selectedIndex == 0) {
			fillServerDetails(new Server());
		} else {
			fillServerDetails(listServers.get(selectedIndex - 1));
		}
	}

	/**
	 * 
	 * @param logontype
	 */
	private void onChangeLogontype(boolean isAnonymous) {
		txtServerUsername.setText("");
		txtServerPassword.setText("");
		txtServerUsername.setEnabled(!isAnonymous);
		txtServerPassword.setEnabled(!isAnonymous);
	}

	/**
	 * Populate site detail
	 */
	private void fillServerDetails(Server server) {

		txtServerName.setText(server.getName());
		txtServerHost.setText(server.getHost());
		txtServerPort.setText(Integer.toString(server.getPort()));

		if (server.getLogontype() == Logontype.ANONYMOUS) {
			rdoLogonTypeAnonymous.setChecked(true);
			rdoLogonTypeNormal.setChecked(false);
			txtServerUsername.setText("");
			txtServerPassword.setText("");
			txtServerUsername.setEnabled(false);
			txtServerPassword.setEnabled(false);
		} else {
			rdoLogonTypeAnonymous.setChecked(false);
			rdoLogonTypeNormal.setChecked(true);
			txtServerUsername.setText(server.getUsername());
			txtServerPassword.setText(server.getPassword());
			txtServerUsername.setEnabled(true);
			txtServerPassword.setEnabled(true);
		}
	}

	/**
	 * 
	 * @param id
	 */
	private void fillServerAdapter(long toSelectId) {

		// Clear list
		if (!serverAdapter.isEmpty()) {
			serverAdapter.clear();
		}

		// Add new server entry
		serverAdapter.add(getString(R.string.server_manager_label_new_server));

		// Add servers items
		int index = 0, selectedIndex = 0;
		for (Server server : listServers) {
			serverAdapter.add(server.getName());

			if (toSelectId == server.getId()) {
				selectedIndex = index + 1;
			}
			index++;
		}

		spnServers.setSelection(selectedIndex);
	}
}
