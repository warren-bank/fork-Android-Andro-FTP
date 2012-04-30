package net.abachar.androftp.transfers.manager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;

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
	private FTPClient ftp;

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

		// Connect
		connect();

		// Star download
		if (ftp != null) {
			try {

				// if (binaryTransfer) {
				ftp.setFileType(FTP.ASCII_FILE_TYPE);
				// }

				// Use passive mode as default because most of us are
				// behind firewalls these days.
				ftp.enterLocalPassiveMode();

				// Go to directory
				if (!ftp.printWorkingDirectory().equals(transfer.getSourcePath())) {
					ftp.changeWorkingDirectory(transfer.getSourcePath());
				}

				// Open local file
				FileOutputStream fos = new FileOutputStream(transfer.getDestinationAbsolutePath());
				CountingOutputStream cos = new CountingOutputStream(fos) {
					protected void beforeWrite(int n) {
						super.beforeWrite(n);

						int p = Math.round((getCount() * 100) / transfer.getFileSize());
						publishProgress(new Integer(p));
						Log.i("DOWN", transfer.getName() + " : -> " + p + "%");
					}
				};
				ftp.retrieveFile(transfer.getName(), cos);

				// Close
				fos.close();
				ftp.logout();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (ftp.isConnected()) {
					try {
						ftp.disconnect();
					} catch (Exception ex) {
						// do nothing
					}
				}
			}
		}
	}

	/**
	 * @see net.abachar.androftp.transfers.manager.TransferTask#doInBackgroundUpload()
	 */
	@Override
	protected void doInBackgroundUpload() {

		// Connect
		connect();

		// Star download
		if (ftp != null) {
			try {

				// if (binaryTransfer) {
				ftp.setFileType(FTP.ASCII_FILE_TYPE);
				// }

				// Use passive mode as default because most of us are
				// behind firewalls these days.
				ftp.enterLocalPassiveMode();

				// Go to directory
				if (!ftp.printWorkingDirectory().equals(transfer.getDestinationPath())) {
					ftp.changeWorkingDirectory(transfer.getDestinationPath());
				}

				// Open local file
				FileInputStream fis = new FileInputStream(transfer.getSourceAbsolutePath());
				CountingInputStream cis = new CountingInputStream(fis) {
					protected void afterRead(int n) {
						super.afterRead(n);

						int sent = getCount() - n;

						int p = Math.round((sent * 100) / transfer.getFileSize());
						publishProgress(new Integer(p));
						Log.i("UP", transfer.getName() + " : -> " + p + "%");
					}
				};
				ftp.storeFile(transfer.getName(), cis);

				// Close
				fis.close();
				ftp.logout();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (ftp.isConnected()) {
					try {
						ftp.disconnect();
					} catch (Exception ex) {
						// do nothing
					}
				}
			}
		}
	}

	/**
	 * 
	 */
	protected void connect() {

		FTPFileManager fileManager = (FTPFileManager) MainApplication.getInstance().getServerFileManager();

		// Connect
		try {

			// New ftp client
			ftp = new FTPClient();
			ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

			// Connect to server
			ftp.connect(fileManager.getHost(), fileManager.getPort());

			// Check the reply code to verify success.
			int reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				return;
			}

			if (fileManager.getLogontype() == Logontype.NORMAL) {
				if (!ftp.login(fileManager.getUsername(), fileManager.getPassword())) {
					ftp.logout();
					return;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			ftp = null;
		}
	}
}
