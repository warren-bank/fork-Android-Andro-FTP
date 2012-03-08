package net.abachar.androftp.servers;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * @author abachar
 */
public class ServerDataSource extends SQLiteOpenHelper {

	/** Database name and version */
	private final static String DATABASE_NAME = "andro_ftp_db";
	private final static int DATABASE_VERSION = 1;

	/** Table and columns names */
	public static final String TABLE_SERVERS = "servers";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_HOST = "host";
	public static final String COLUMN_PORT = "port";
	public static final String COLUMN_LOGONTYPE = "logontype";
	public static final String COLUMN_USERNAME = "username";
	public static final String COLUMN_PASSWORD = "password";

	/** atabase creation sql statement */
	private final static String CREATE_QUERY = "CREATE TABLE " + TABLE_SERVERS + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME + " TEXT NOT NULL UNIQUE, " + COLUMN_HOST
			+ " VARCHAR(100) NOT NULL, " + COLUMN_PORT + " INTEGER NOT NULL, " + COLUMN_LOGONTYPE + " INTEGER NOT NULL, " + COLUMN_USERNAME + " VARCHAR(100), " + COLUMN_PASSWORD + " VARCHAR(100))";

	/** Constructor with context */
	public ServerDataSource(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_QUERY);

	}

	/**
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase,
	 *      int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	/**
	 * @return list of all stored servers
	 */
	public List<Server> getAllServers() {
		List<Server> servers = new ArrayList<Server>();

		// Get database connexion
		SQLiteDatabase db = getReadableDatabase();

		// Open cursor
		Cursor cursor = db.query(TABLE_SERVERS, new String[] { COLUMN_ID, COLUMN_NAME, COLUMN_HOST, COLUMN_PORT, COLUMN_LOGONTYPE, COLUMN_USERNAME, COLUMN_PASSWORD }, null, null, null, null,
				COLUMN_NAME + " ASC");
		if (cursor != null) {
			while (cursor.moveToNext()) {
				Server server = new Server();
				server.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
				server.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
				server.setHost(cursor.getString(cursor.getColumnIndex(COLUMN_HOST)));
				server.setPort(cursor.getInt(cursor.getColumnIndex(COLUMN_PORT)));
				server.setLogontype(cursor.getInt(cursor.getColumnIndex(COLUMN_LOGONTYPE)));
				server.setUsername(cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME)));
				server.setPassword(cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD)));
				servers.add(server);
			}
		}

		return servers;
	}

	/**
	 * 
	 */
	public Server saveServer(Server server) {

		// Get database connexion
		SQLiteDatabase db = getWritableDatabase();

		// Data to save
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME, server.getName());
		values.put(COLUMN_HOST, server.getHost());
		values.put(COLUMN_PORT, server.getPort());
		values.put(COLUMN_LOGONTYPE, server.getLogontype());
		values.put(COLUMN_USERNAME, server.getUsername());
		values.put(COLUMN_PASSWORD, server.getPassword());

		// Insert if new or just update
		if (server.getId() == -1) {
			long id = db.insert(TABLE_SERVERS, null, values);
			server.setId(id);
		} else {
			db.update("accounts", values, COLUMN_ID + " = " + server.getId(), null);
		}

		return server;
	}

	/**
	 * 
	 */
	public boolean deleteServer(long id) {

		// Get database connexion
		SQLiteDatabase db = getWritableDatabase();

		// Get database connexion
		return db.delete(TABLE_SERVERS, COLUMN_ID + " = " + id, null) == 1;
	}
}
