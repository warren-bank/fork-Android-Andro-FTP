package net.abachar.androftp.filelist.manager;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;

import net.abachar.androftp.servers.Logontype;
import net.abachar.androftp.util.Constants;
import net.abachar.androftp.util.FileType;

import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPReply;

import android.os.Bundle;

public class FTPFileManager extends AbstractFileManager {

	/**
	 * 
	 */
	private FTPClient mFTPClient;

	/**
	 * Server configuration
	 */
	private String mHost;
	private int mPort;
	private Logontype mLogontype;
	private String mUsername;
	private String mPassword;
	private int mTimeout;
	private int mMaxNbrRetires;
	private int mDelayBetweenFailedLogin;

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#init(android.os.Bundle)
	 */
	@Override
	public void init(Bundle bundle) {

		// Initial order
		if (bundle.containsKey("server.orderBy")) {
			mOrderByComparator = new OrderByComparator((OrderBy) bundle.get("server.orderBy"));
		}

		// Server configuration
		mHost = bundle.getString("server.host");
		mPort = bundle.getInt("server.port");
		mLogontype = (Logontype) bundle.get("server.logontype");
		if (mLogontype == Logontype.NORMAL) {
			mUsername = bundle.getString("server.username");
			mPassword = bundle.getString("server.password");
		} else {
			mUsername = null;
			mPassword = null;
		}

		// Time out
		mTimeout = bundle.getInt(Constants.PREFERENCE_TIMEOUT) * 1000;
		mDelayBetweenFailedLogin = bundle.getInt(Constants.PREFERENCE_DELAY_BETWEEN_FAILED_LOGIN) * 1000;
		mMaxNbrRetires = bundle.getInt(Constants.PREFERENCE_MAX_NBR_RETRIES);

		// Not connected
		mRootPath = mCurrentPath = "";
		mInRootFolder = true;
	}

	/**
	 * 
	 * @return
	 * @throws ConnectionException
	 */
	public FTPClient getConnection(final BackgroundOperationListener listener) throws FileManagerException {

		int count = mMaxNbrRetires;

		do {

			FTPClient ftpClient = null;
			try {
				ftpClient = new FTPClient();

				// Setup listner
				if (listener != null) {
					ftpClient.addProtocolCommandListener(new LogProtocolCommandListener(listener));
				}

				// Timeout
				ftpClient.setConnectTimeout(mTimeout);
				ftpClient.setDataTimeout(mTimeout);

				// Connect to server
				ftpClient.connect(mHost, mPort);

				// Check the reply code to verify success.
				int reply = ftpClient.getReplyCode();
				if (!FTPReply.isPositiveCompletion(reply)) {
					throw new FileManagerException(FileManagerEvent.ERR_CONNECTION);
				}

				if (mLogontype == Logontype.NORMAL) {
					if (!ftpClient.login(mUsername, mPassword)) {
						ftpClient.logout();
						throw new FileManagerException(FileManagerEvent.ERR_CONNECTION);
					}
				}

				// Use passive mode as default because most of us are
				// behind firewalls these days.
				ftpClient.enterLocalPassiveMode();

				return ftpClient;
			} catch (SocketException e) {

				// StringBuilder sb = new StringBuilder("x ");
				// sb.append(e.getMessage());
				// listener.onPublishProgress(new
				// FileManagerEvent(FileManagerEvent.LOG_CONNECT,
				// e.getMessage()));

			} catch (IOException e) {

				// StringBuilder sb = new StringBuilder("x ");
				// sb.append(e.getMessage());
				// listener.onPublishProgress(new
				// FileManagerEvent(FileManagerEvent.LOG_CONNECT,
				// e.getMessage()));

			} catch (FileManagerException e) {
				try {
					if (ftpClient != null) {
						ftpClient.disconnect();
					}
				} catch (IOException e1) {
				}
			}

			count--;

			// Sleep before retry
			if (count > 0) {
				listener.onPublishProgress(new FileManagerEvent(FileManagerEvent.LOG_CONNECT, "Status: Waiting to retry..."));
				try {
					Thread.sleep(mDelayBetweenFailedLogin);
				} catch (InterruptedException e) {
				}
			}

		} while (count > 0);

		// We have reached the maximum number of attempts
		throw new FileManagerException(FileManagerEvent.ERR_CONNECTION);
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#doConnect()
	 */
	protected void doConnect(BackgroundOperationListener listener) throws FileManagerException {

		// Connect
		try {

			// New ftp client
			mFTPClient = getConnection(listener);
			mConnected = true;

			// Paths
			mRootPath = mCurrentPath = mFTPClient.printWorkingDirectory();

			// Load files
			loadFiles();

		} catch (IOException e) {
			throw new FileManagerException(FileManagerEvent.ERR_LOST_CONNECTION);
		} catch (FileManagerException e) {
			mFTPClient = null;
			mConnected = false;
			throw new FileManagerException(FileManagerEvent.ERR_CONNECTION);
		}
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#doChangeToParentDirectory()
	 */
	@Override
	protected void doChangeToParentDirectory(BackgroundOperationListener listener) throws FileManagerException {

		try {
			if (mFTPClient.changeToParentDirectory()) {
				mCurrentPath = mFTPClient.printWorkingDirectory();

				// refresh list files
				mInRootFolder = mRootPath.equals(mCurrentPath);
				loadFiles();
			}
		} catch (FTPConnectionClosedException e) {
			throw new FileManagerException(FileManagerEvent.ERR_LOST_CONNECTION);
		} catch (IOException e) {
			throw new FileManagerException(FileManagerEvent.ERR_LOST_CONNECTION);
		}
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.FileManager#doChangeWorkingDirectory(net.abachar.androftp.filelist.manager.FileEntry)
	 */
	@Override
	protected void doChangeWorkingDirectory(BackgroundOperationListener listener, FileEntry dir) throws FileManagerException {

		try {
			if (mFTPClient.changeWorkingDirectory(dir.getAbsolutePath())) {
				mCurrentPath = mFTPClient.printWorkingDirectory();

				// refresh list files
				mInRootFolder = mRootPath.equals(mCurrentPath);
				loadFiles();
			}
		} catch (FTPConnectionClosedException e) {
			throw new FileManagerException(FileManagerEvent.ERR_LOST_CONNECTION);
		} catch (IOException e) {
			throw new FileManagerException(FileManagerEvent.ERR_LOST_CONNECTION);
		}
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.AbstractFileManager#doDeleteFiles(net.abachar.androftp.filelist.manager.FileEntry[])
	 */
	@Override
	protected void doDeleteFiles(BackgroundOperationListener listener, FileEntry[] files) throws FileManagerException {

		try {
			for (FileEntry file : files) {
				if (file.isFolder()) {
					if (!mFTPClient.removeDirectory(file.getName())) {
						throw new FileManagerException(FileManagerEvent.ERR_DELETE_FILE);
					}
				} else {
					if (!mFTPClient.deleteFile(file.getName())) {
						throw new FileManagerException(FileManagerEvent.ERR_DELETE_FOLDER);
					}
				}
			}

			// Refresh file list
			loadFiles();

		} catch (FTPConnectionClosedException e) {
			throw new FileManagerException(FileManagerEvent.ERR_LOST_CONNECTION);
		} catch (IOException e) {
			throw new FileManagerException(FileManagerEvent.ERR_LOST_CONNECTION);
		}
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.AbstractFileManager#doCreateNewfolder(net.abachar.androftp.filelist.manager.FileEntry)
	 */
	@Override
	protected void doCreateNewfolder(BackgroundOperationListener listener, FileEntry dir) throws FileManagerException {

		try {

			// Make directory
			if (mFTPClient.makeDirectory(dir.getName())) {
				loadFiles();
			} else {
				throw new FileManagerException(FileManagerEvent.ERR_CREATE_FOLDER);
			}

		} catch (FTPConnectionClosedException e) {
			throw new FileManagerException(FileManagerEvent.ERR_LOST_CONNECTION);
		} catch (IOException e) {
			throw new FileManagerException(FileManagerEvent.ERR_LOST_CONNECTION);
		}
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.AbstractFileManager#doRefresh()
	 */
	@Override
	protected void doRefresh(BackgroundOperationListener listener) throws FileManagerException {

		try {
			// Refresh file list
			loadFiles();

		} catch (FTPConnectionClosedException e) {
			throw new FileManagerException(FileManagerEvent.ERR_LOST_CONNECTION);
		} catch (IOException e) {
			throw new FileManagerException(FileManagerEvent.ERR_LOST_CONNECTION);
		}
	}

	/**
	 * @see net.abachar.androftp.filelist.manager.AbstractFileManager#doRenameFile(net.abachar.androftp.filelist.manager.FileEntry,
	 *      net.abachar.androftp.filelist.manager.FileEntry)
	 */
	@Override
	protected void doRenameFile(BackgroundOperationListener listener, FileEntry file, FileEntry newFile) throws FileManagerException {

		try {

			if (mFTPClient.rename(file.getName(), newFile.getName())) {
				loadFiles();
			} else {
				if (file.isFolder()) {
					throw new FileManagerException(FileManagerEvent.ERR_RENAME_FOLDER);
				} else {
					throw new FileManagerException(FileManagerEvent.ERR_RENAME_FILE);
				}
			}

		} catch (FTPConnectionClosedException e) {
			throw new FileManagerException(FileManagerEvent.ERR_LOST_CONNECTION);
		} catch (IOException e) {
			throw new FileManagerException(FileManagerEvent.ERR_LOST_CONNECTION);
		}

	}

	/**
	 * @throws IOException
	 * 
	 */
	private void loadFiles() throws IOException {
		mFileList = null;

		// Load server files
		FTPFile[] list = mFTPClient.listFiles(mCurrentPath, new FTPFileFilter() {
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
			mFileList = new ArrayList<FileEntry>();
			for (FTPFile sf : list) {
				FileEntry df = new FileEntry();
				df.setName(sf.getName());
				if (mCurrentPath.endsWith(File.separator)) {
					df.setAbsolutePath(mCurrentPath + sf.getName());
				} else {
					df.setAbsolutePath(mCurrentPath + File.separator + sf.getName());
				}
				df.setParentPath(mCurrentPath);
				df.setSize(sf.getSize());
				df.setType(FileType.fromFTPFile(sf));
				df.setLastModified(sf.getTimestamp().getTimeInMillis());

				mFileList.add(df);
			}

			// Sort
			Collections.sort(mFileList, mOrderByComparator);
		}
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return mHost;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return mPort;
	}

	/**
	 * @return the logontype
	 */
	public Logontype getLogontype() {
		return mLogontype;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return mUsername;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return mPassword;
	}

	/**
	 * @return the mTimeout
	 */
	public int getTimeout() {
		return mTimeout;
	}

	/**
	 * @author abachar
	 */
	class LogProtocolCommandListener implements ProtocolCommandListener {

		/** */
		private BackgroundOperationListener mBackgroundOperationListener;

		/**
		 * @param mBackgroundOperationListener
		 */
		private LogProtocolCommandListener(BackgroundOperationListener mBackgroundOperationListener) {
			this.mBackgroundOperationListener = mBackgroundOperationListener;
		}

		/**
		 * @see org.apache.commons.net.ProtocolCommandListener#protocolCommandSent(org.apache.commons.net.ProtocolCommandEvent)
		 */
		@Override
		public void protocolCommandSent(ProtocolCommandEvent event) {
			StringBuilder sb = new StringBuilder("Command: ");
			sb.append(event.getMessage());

			// Send event
			mBackgroundOperationListener.onPublishProgress(new FileManagerEvent(FileManagerEvent.LOG_CONNECT, sb.toString()));
		}

		/**
		 * @see org.apache.commons.net.ProtocolCommandListener#protocolReplyReceived(org.apache.commons.net.ProtocolCommandEvent)
		 */
		@Override
		public void protocolReplyReceived(ProtocolCommandEvent event) {

			StringBuilder sb = new StringBuilder();
			String lines[] = event.getMessage().split("\\r?\\n");
			for (String line : lines) {
				sb.append("Response: ").append(line).append("\\n");
			}

			// Send event
			mBackgroundOperationListener.onPublishProgress(new FileManagerEvent(FileManagerEvent.LOG_CONNECT, sb.toString()));
		}
	}
}
