package net.abachar.androftp.filelist;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;

import net.abachar.androftp.servers.Logontype;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPReply;

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
	public FTPFileManager() {
		super();
	}

	/**
	 * @see net.abachar.androftp.filelist.FileManager#init(android.os.Bundle)
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
	 * @see net.abachar.androftp.filelist.FileManager#doConnect()
	 */
	protected boolean doConnect() {

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Connect
		try {
			ftpClient = new FTPClient();
			ftpClient.connect(host, port);

			// Check the reply code to verify success.
			int reply = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftpClient.disconnect();
			} else {

				if (logontype == Logontype.NORMAL) {
					if (!ftpClient.login(username, password)) {
						ftpClient.logout();
					}
				}

				ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
				ftpClient.enterLocalPassiveMode();

				rootPath = currentPath = ftpClient.printWorkingDirectory();

				// Load files
				loadFiles();
				notifyListeners(FileManagerMessage.INITIAL_LIST_FILES);
				return true;
			}

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
		}

		return false;
	}

	/**
	 * @see net.abachar.androftp.filelist.FileManager#doChangeToParentDirectory()
	 */
	@Override
	protected void doChangeToParentDirectory() {

		// refresh list files
		try {
			if (ftpClient.changeToParentDirectory()) {
				currentPath = ftpClient.printWorkingDirectory();

				// refresh list files
				inRootFolder = rootPath.equals(currentPath);
				loadFiles();
			}
		} catch (IOException e) {
		}
	}

	/**
	 * @see net.abachar.androftp.filelist.FileManager#doChangeWorkingDirectory(java.util.String)
	 */
	@Override
	protected void doChangeWorkingDirectory(String dirname) {

		// Change working directory
		try {
			if (ftpClient.changeWorkingDirectory(currentPath + File.separator + dirname)) {
				currentPath = ftpClient.printWorkingDirectory();

				// refresh list files
				inRootFolder = rootPath.equals(currentPath);
				loadFiles();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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
