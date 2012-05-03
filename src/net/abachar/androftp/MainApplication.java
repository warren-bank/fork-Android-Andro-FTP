package net.abachar.androftp;

import net.abachar.androftp.filelist.manager.FTPFileManager;
import net.abachar.androftp.filelist.manager.FileManager;
import net.abachar.androftp.filelist.manager.LocalFileManager;
import net.abachar.androftp.transfers.manager.TransferManager;
import android.app.Application;
import android.os.Bundle;

/**
 * 
 * @author abachar
 */
public class MainApplication extends Application {

	/** Shared preferences file */
	public static final String SHARED_PREFERENCES_NAME = "AndroFTPSharedPreferences";

	/** Unique instance */
	private static MainApplication mInstance;

	/** Manages */
	private FileManager mLocalFileManager;
	private FileManager mServerFileManager;
	private TransferManager mTransferManager;

	/**
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
	}

	/**
	 * 
	 * @param context
	 * @param bundle
	 */
	public void initManagers(MainActivity context, Bundle bundle) {

		// Local manager
		mLocalFileManager = new LocalFileManager(context);
		mLocalFileManager.addListener(context);
		mLocalFileManager.init(bundle);

		// Server manager
		mServerFileManager = new FTPFileManager(context);
		mServerFileManager.addListener(context);
		mServerFileManager.init(bundle);

		// Transfers
		mTransferManager = new TransferManager();
	}

	/**
	 * 
	 */
	public void connect() {
		mLocalFileManager.connect();
		mServerFileManager.connect();
	}

	/**
	 * 
	 * @return
	 */
	public static MainApplication getInstance() {
		return mInstance;
	}

	/**
	 * @return the localFileManager
	 */
	public FileManager getLocalFileManager() {
		return mLocalFileManager;
	}

	/**
	 * @return the serverFileManager
	 */
	public FileManager getServerFileManager() {
		return mServerFileManager;
	}

	/**
	 * @return the mTransferManager
	 */
	public TransferManager getTransferManager() {
		return mTransferManager;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isAllConnected() {
		return mLocalFileManager.isConnected() && mServerFileManager.isConnected();
	}
}
