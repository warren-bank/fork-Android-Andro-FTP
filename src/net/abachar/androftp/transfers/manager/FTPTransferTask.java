package net.abachar.androftp.transfers.manager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.List;

import net.abachar.androftp.MainApplication;
import net.abachar.androftp.filelist.manager.FTPFileManager;
import net.abachar.androftp.servers.Logontype;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

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
	public FTPTransferTask(TransferTaskProgressListener progressListener, List<Transfer> transferList) {
		super(progressListener, transferList);
	}

	/**
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected String doInBackground(Transfer... transfers) {

		// Create ftp client
		mFTPClient = new FTPClient();
		mFTPClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

		String retval = super.doInBackground(transfers);

		// Disconnect ftp client
		if ((mFTPClient != null) && mFTPClient.isConnected()) {
			try {
				mFTPClient.logout();
				mFTPClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return retval;
	}

	/**
	 * @see net.abachar.androftp.transfers.manager.TransferTask#doInBackgroundDownload()
	 */
	@Override
	protected void doInBackgroundDownload() {

		try {
			// Connect it if disconnected
			if (!mFTPClient.isConnected()) {
				connect();
			}

			// if (binaryTransfer) {
			mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
			// }

			// Go to directory
			if (!mFTPClient.printWorkingDirectory().equals(mCurrentTransfer.getSourcePath())) {
				mFTPClient.changeWorkingDirectory(mCurrentTransfer.getSourcePath());
			}

			// Open local file
			FileOutputStream fos = new FileOutputStream(mCurrentTransfer.getFullDestinationPath());
			CountingOutputStream cos = new CountingOutputStream(fos) {
				protected void beforeWrite(int n) {
					super.beforeWrite(n);

					int progress = Math.round((getCount() * 100) / mCurrentTransfer.getFileSize());
					mCurrentTransfer.setProgress(progress);
					publishProgress(mCurrentTransfer.getId(), progress);
				}
			};

			// Download file
			mFTPClient.retrieveFile(mCurrentTransfer.getName(), cos);

			// Close local file
			fos.close();

			// End of transfer
			publishProgress(mCurrentTransfer.getId(), 101);

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see net.abachar.androftp.transfers.manager.TransferTask#doInBackgroundUpload()
	 */
	@Override
	protected void doInBackgroundUpload() {

		try {
			// Connect it if disconnected
			if (!mFTPClient.isConnected()) {
				connect();
			}

			// if (binaryTransfer) {
			mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
			// }

			// Open local file
			FileInputStream fis = new FileInputStream(mCurrentTransfer.getFullSourcePath());
			CountingInputStream cis = new CountingInputStream(fis) {
				protected void afterRead(int n) {
					super.afterRead(n);

					int progress = Math.round((getCount() * 100) / mCurrentTransfer.getFileSize());
					mCurrentTransfer.setProgress(progress);
					publishProgress(mCurrentTransfer.getId(), progress);
				}
			};

			// Go to directory
			if (!mFTPClient.printWorkingDirectory().equals(mCurrentTransfer.getDestinationPath())) {
				mFTPClient.changeWorkingDirectory(mCurrentTransfer.getDestinationPath());
			}

			// Upload file
			mFTPClient.storeFile(mCurrentTransfer.getName(), cis);

			// Close local file
			fis.close();

			// End of transfer
			publishProgress(mCurrentTransfer.getId(), 101);

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

		// Use passive mode as default because most of us are
		// behind firewalls these days.
		mFTPClient.enterLocalPassiveMode();
	}
}
