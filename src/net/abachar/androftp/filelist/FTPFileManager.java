package net.abachar.androftp.filelist;

import java.util.ArrayList;
import java.util.List;

import net.abachar.androftp.servers.Logontype;
import android.os.Bundle;

public class FTPFileManager extends AbstractFileManager {

	/**
	 * Server configuration
	 */
	private String host;
	private int port;
	private Logontype logontype;
	private String username;
	private String password;

	/**
	 * @see net.abachar.androftp.filelist.FileManager#init(java.util.Map)
	 */
	public FTPFileManager(Bundle bundle) {
		super(bundle);

		// Initial order
		orderByComparator = new OrderByComparator((OrderBy) bundle.get("server.orderBy"));

		// Server configuration
		host = bundle.getString("server.rootPath");
		port = bundle.getInt("server.port");
		logontype = (Logontype) bundle.get("server.logontype");
		if (logontype == Logontype.NORMAL) {
			username = bundle.getString("server.username");
			password = bundle.getString("server.password");
		} else {
			username = null;
			password = null;
		}

		//
		files = new ArrayList<FileEntry>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.abachar.androftp.filelist.FileManager#goParent()
	 */
	@Override
	public void goParent() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.abachar.androftp.filelist.FileManager#cwd(java.lang.String)
	 */
	@Override
	public void cwd(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	protected List<FileEntry> loadFiles() {
		// TODO Auto-generated method stub
		return null;
	}

}
