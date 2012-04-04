package net.abachar.androftp.filelist.manager;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;

import net.abachar.androftp.servers.Logontype;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPReply;

import android.content.Context;
import android.os.Bundle;

public class FTPFileManager extends AbstractFileManager {

	/**
	 * 
	 */
	private FTPClient ftpClient;

	/**
	 * Server configuration
	 */
	private String host;
	private int port;
	private Logontype logontype;
	private String username;
	private String password;

	/**
	 * Default constructor
	 */
	public FTPFileManager(Context context) {
		super(context);
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#init(android.os.Bundle)
	 */
	@Override
	public void init(Bundle bundle) {

		// Initial order
		if (bundle.containsKey("server.orderBy")) {
			orderByComparator = new OrderByComparator((OrderBy) bundle.get("server.orderBy"));
		}

		// Server configuration
		host = bundle.getString("server.host");
		port = bundle.getInt("server.port");
		logontype = (Logontype) bundle.get("server.logontype");
		if (logontype == Logontype.NORMAL) {
			username = bundle.getString("server.username");
			password = bundle.getString("server.password");
		} else {
			username = null;
			password = null;
		}

		// Not connected
		rootPath = currentPath = "";
		inRootFolder = true;
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#doConnect()
	 */
	protected void doConnect() throws FileManagerException {

		// Connect
		try {

			// For test :)
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// New ftp client
			ftpClient = new FTPClient();

			// Connect to server
			ftpClient.connect(host, port);

			// Check the reply code to verify success.
			int reply = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				throw new ConnectionException("E0121");
			}

			if (logontype == Logontype.NORMAL) {
				if (!ftpClient.login(username, password)) {
					ftpClient.logout();
					throw new ConnectionException("E0122");
				}
			}

			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			ftpClient.enterLocalPassiveMode();

			rootPath = currentPath = ftpClient.printWorkingDirectory();

			// Load files
			loadFiles();
			notifyListeners(FileManagerEvent.INITIAL_LIST_FILES);

		} catch (SocketException e) {
			throw new ConnectionException("E0101");
		} catch (IOException e) {
			throw new ConnectionException("E0102");
		} catch (ConnectionException e) {
			try {
				ftpClient.disconnect();
			} catch (IOException e1) {
			}

			throw e;
		}
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#doChangeToParentDirectory()
	 */
	@Override
	protected void doChangeToParentDirectory() throws FileManagerException {

		// refresh list files
		try {
			if (ftpClient.changeToParentDirectory()) {
				currentPath = ftpClient.printWorkingDirectory();

				// refresh list files
				inRootFolder = rootPath.equals(currentPath);
				loadFiles();
			}
		} catch (FTPConnectionClosedException e) {
			throw new ConnectionException("E0151", e);
		} catch (IOException e) {
			throw new FileManagerException("E0161", e);
		}
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#doChangeWorkingDirectory(java.util.String)
	 */
	@Override
	protected void doChangeWorkingDirectory(String dirname) throws FileManagerException {

		// Change working directory
		try {
			if (ftpClient.changeWorkingDirectory(currentPath + File.separator + dirname)) {
				currentPath = ftpClient.printWorkingDirectory();

				// refresh list files
				inRootFolder = rootPath.equals(currentPath);
				loadFiles();
			}
		} catch (FTPConnectionClosedException e) {
			throw new ConnectionException("E0151", e);
		} catch (IOException e) {
			throw new FileManagerException("E0161", e);
		}
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.AbstractFileManager#doRefresh()
	 */
	@Override
	protected void doRefresh() throws FileManagerException {
		// TODO Auto-generated method stub

	}

	/**
	 * @see net.abachar.androftp.filelist.manager.AbstractFileManager#doCreateNewfolder(java.lang.String)
	 */
	@Override
	protected void doCreateNewfolder(String name) throws FileManagerException {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 */
	private void loadFiles() {
		files = null;

		// Load server files
		try {
			FTPFile[] list = ftpClient.listFiles(currentPath, new FTPFileFilter() {
				@Override
				public boolean accept(FTPFile file) {
					String fileName = file.getName();

					if (file.isDirectory()) {
						return !".".equals(fileName) && !"..".equals(fileName);
					}

					return !file.isSymbolicLink();
				}
			});

			// Scan all files
			if ((list != null) && (list.length > 0)) {
				files = new ArrayList<FileEntry>();
				for (FTPFile sf : list) {
					FileEntry df = new FileEntry();
					df.setName(sf.getName());
					df.setPath(currentPath + File.separator + sf.getName());
					df.setSize(sf.getSize());
					df.setType(FileType.fromFTPFile(sf));
					df.setLastModified(sf.getTimestamp().getTimeInMillis());

					files.add(df);
				}

				// Sort
				Collections.sort(files, orderByComparator);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
