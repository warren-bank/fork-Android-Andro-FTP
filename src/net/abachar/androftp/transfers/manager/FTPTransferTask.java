package net.abachar.androftp.transfers.manager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.List;

import net.abachar.androftp.MainApplication;
import net.abachar.androftp.filelist.manager.FTPFileManager;
import net.abachar.androftp.filelist.manager.FileManagerException;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

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

			// File type
			if (ASCII_FILE.isAsciiFile(mCurrentTransfer.getName())) {
				mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
			} else {
				mFTPClient.setFileType(FTP.ASCII_FILE_TYPE);
			}

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

			// File type
			if (ASCII_FILE.isAsciiFile(mCurrentTransfer.getName())) {
				mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
			} else {
				mFTPClient.setFileType(FTP.ASCII_FILE_TYPE);
			}

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

		// Get connection
		try {
			mFTPClient = fileManager.getConnection();
		} catch (FileManagerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Ascii files extensions : TODO Move to net.abachar.androftp.util.FileType
	 */
	static enum ASCII_FILE {
		AM("am"), ASP("asp"), BAT("bat"), C("c"), CFM("cfm"), CGI("cgi"), CONF("conf"), CPP("cpp"), CSS("css"), DHTML("dhtml"), DIZ("diz"), H("h"), HPP("hpp"), HTM("htm"), HTML("html"), IN("in"), INC(
				"inc"), JAVA("java"), JS("js"), JSP("jsp"), LUA("lua"), M4("m4"), MAK("mak"), MD5("md5"), NFO("nfo"), NSI("nsi"), PAS("pas"), PATCH("patch"), PHP("php"), PHTML("phtml"), PL("pl"), PO(
				"po"), PY("py"), QMAIL("qmail"), SH("sh"), SHTML("shtml"), SQL("sql"), SVG("svg"), TCL("tcl"), TPL("tpl"), TXT("txt"), VBS("vbs"), XHTML("xhtml"), XML("xml"), XRC("xrc");

		/** */
		String value;

		/** */
		private ASCII_FILE(String value) {
			this.value = value;
		}

		/** */
		public static boolean isAsciiFile(String fileName) {

			// Get extension
			int lastDot = fileName.lastIndexOf('.');
			if (lastDot >= 0) {

				// By extension
				String fext = fileName.substring(lastDot + 1).toLowerCase();
				for (ASCII_FILE af : ASCII_FILE.values()) {
					if (fext.equals(af.value)) {
						return true;
					}
				}
			}

			return false;
		}
	}
}
