package net.abachar.androftp.transfers.manager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;

import net.abachar.androftp.MainApplication;
import net.abachar.androftp.filelist.manager.FTPFileManager;
import net.abachar.androftp.servers.Logontype;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import android.util.Log;

/**
 * 
 * @author abachar
 */
public class FTPTransferTask extends TransferTask {

	/** */
	private FTPClient mFTPClient;

	/**
	 *
	 */
	public FTPTransferTask(TransferTaskProgressListener progressListener) {
		super(progressListener);
	}

	/**
	 * @see net.abachar.androftp.transfers.manager.TransferTask#doInBackgroundDownload()
	 */
	@Override
	protected void doInBackgroundDownload() {

		try {
			// Create client
			if (mFTPClient == null) {
				createFTPClient();
			}

			if (!mFTPClient.isConnected()) {
				connect();
			}

			// if (binaryTransfer) {
			mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
			// }

			// Use passive mode as default because most of us are
			// behind firewalls these days.
			mFTPClient.enterLocalPassiveMode();

			// Go to directory
			if (!mFTPClient.printWorkingDirectory().equals(mTransfer.getSourcePath())) {
				mFTPClient.changeWorkingDirectory(mTransfer.getSourcePath());
			}

			// Open local file
			FileOutputStream fos = new FileOutputStream(mTransfer.getFullDestinationPath());
			CountingOutputStream cos = new CountingOutputStream(fos) {
				protected void beforeWrite(int n) {
					super.beforeWrite(n);

					int p = Math.round((getCount() * 100) / mTransfer.getFileSize());
					publishProgress(new Integer(p));
					Log.i("DOWN", mTransfer.getName() + " : -> " + p + "%");
				}
			};
			mFTPClient.retrieveFile(mTransfer.getName(), cos);

			// Close
			fos.close();
			mFTPClient.logout();

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if ((mFTPClient == null) && mFTPClient.isConnected()) {
				try {
					mFTPClient.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @see net.abachar.androftp.transfers.manager.TransferTask#doInBackgroundUpload()
	 */
	@Override
	protected void doInBackgroundUpload() {

		try {
			// Create client
			if (mFTPClient == null) {
				createFTPClient();
			}

			if (!mFTPClient.isConnected()) {
				connect();
			}

			// if (binaryTransfer) {
			mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
			// }

			// Use passive mode as default because most of us are
			// behind firewalls these days.
			mFTPClient.enterLocalPassiveMode();

			// Go to directory
			if (!mFTPClient.printWorkingDirectory().equals(mTransfer.getDestinationPath())) {
				mFTPClient.changeWorkingDirectory(mTransfer.getDestinationPath());
			}

			// Open local file
			FileInputStream fis = new FileInputStream(mTransfer.getFullSourcePath());
			CountingInputStream cis = new CountingInputStream(fis) {
				protected void afterRead(int n) {
					super.afterRead(n);

					int sent = getCount() - n;

					int p = Math.round((sent * 100) / mTransfer.getFileSize());
					publishProgress(new Integer(p));
					Log.i("UP", mTransfer.getName() + " : -> " + p + "%");
				}
			};
			mFTPClient.storeFile(mTransfer.getName(), cis);

			// Close
			fis.close();

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private void createFTPClient() {
		mFTPClient = new FTPClient();
		mFTPClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
	}

	/**
	 * @throws IOException
	 * @throws SocketException
	 * 
	 */
	private void connect() throws SocketException, IOException {

		FTPFileManager fileManager = (FTPFileManager) MainApplication.getInstance().getServerFileManager();

		// Connect to server
		mFTPClient.connect(fileManager.getHost(), fileManager.getPort());

		// Check the reply code to verify success.
		int reply = mFTPClient.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			return;
		}

		if (fileManager.getLogontype() == Logontype.NORMAL) {
			if (!mFTPClient.login(fileManager.getUsername(), fileManager.getPassword())) {
				mFTPClient.logout();
				return;
			}
		}
	}
}
