package net.abachar.androftp.filelist;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import net.abachar.androftp.servers.Logontype;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
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
		super.init(bundle);

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

		// Connect
		try {
			ftpClient = new FTPClient();
			ftpClient.connect(host, port);

			// Check the reply code to verify success.
			int reply = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftpClient.disconnect();
			}

			if (logontype == Logontype.NORMAL) {
				if (!ftpClient.login(username, password)) {
					ftpClient.logout();
				}
			}

			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			ftpClient.enterLocalPassiveMode();

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Update list files
		try {
			rootPath = currentPath = ftpClient.printWorkingDirectory();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		updateListFiles(true);
	}

	/**
	 * @see net.abachar.androftp.filelist.FileManager#goParent()
	 */
	@Override
	public void goParent() {

		// refresh list files
		try {
			currentPath = paths.pop();
			ftpClient.changeToParentDirectory();
		} catch (IOException e) {
		}
		updateListFiles(true);
	}

	/**
	 * @see net.abachar.androftp.filelist.FileManager#cwd(java.lang.String)
	 */
	@Override
	public void cwd(String name) {

		// Push current path in stack
		paths.push(currentPath);

		// refresh list files
		currentPath = currentPath + File.separator + name;
		try {
			ftpClient.changeWorkingDirectory(currentPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		updateListFiles(true);
	}

	/**
	 * @see net.abachar.androftp.filelist.FileManager#loadFiles()
	 */
	@Override
	protected List<FileEntry> loadFiles() {

		List<FileEntry> files = null;

		// Load local files
		try {
			FTPFile[] list = ftpClient.listFiles(currentPath);

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
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return files;
	}
}
