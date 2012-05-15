package net.abachar.androftp;

import net.abachar.androftp.filelist.manager.FTPFileManager;
import net.abachar.androftp.filelist.manager.FileManager;
import net.abachar.androftp.filelist.manager.LocalFileManager;
import net.abachar.androftp.transfers.manager.TransferManager;
import net.abachar.androftp.util.Constants;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

/**
 * 
 * @author abachar
 */
public class MainApplication extends Application {

	/** Unique instance */
	private static MainApplication mInstance;

	/** Shared preferences */
	private SharedPreferences mSharedPreferences;

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

		// Load shared preferences
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
	}

	/**
	 * 
	 * @param context
	 * @param bundle
	 */
	public void initManagers(MainActivity context, Bundle bundle) {

		// Load preferences
		loadPreferences(bundle);

		// Local manager
		mLocalFileManager = new LocalFileManager();
		mLocalFileManager.addListener(context);
		mLocalFileManager.init(bundle);

		// Server manager
		mServerFileManager = new FTPFileManager();
		mServerFileManager.addListener(context);
		mServerFileManager.init(bundle);

		// Transfers
		mTransferManager = new TransferManager();
		mTransferManager.init(bundle);
	}

	/**
	 * 
	 * @param bundle
	 */
	private void loadPreferences(Bundle bundle) {

		// Timeout
		String timeoutPreference = mSharedPreferences.getString("timeout_preference", null);
		if (timeoutPreference != null) {
			bundle.putInt(Constants.PREFERENCE_TIMEOUT, Integer.parseInt(timeoutPreference));
		} else {
			bundle.putInt(Constants.PREFERENCE_TIMEOUT, Constants.DEFAULT_TIMEOUT);
		}

		// Max number retries
		String maxNbrRetriesPreference = mSharedPreferences.getString("max_nbr_retries_preference", null);
		if (timeoutPreference != null) {
			bundle.putInt(Constants.PREFERENCE_MAX_NBR_RETRIES, Integer.parseInt(maxNbrRetriesPreference));
		} else {
			bundle.putInt(Constants.PREFERENCE_MAX_NBR_RETRIES, Constants.DEFAULT_MAX_NBR_RETRIES);
		}

		// Delay between failed login
		String delayBetweenFailedLoginPreference = mSharedPreferences.getString("delay_between_failed_login_preference", null);
		if (delayBetweenFailedLoginPreference != null) {
			bundle.putInt(Constants.PREFERENCE_DELAY_BETWEEN_FAILED_LOGIN, Integer.parseInt(delayBetweenFailedLoginPreference));
		} else {
			bundle.putInt(Constants.PREFERENCE_DELAY_BETWEEN_FAILED_LOGIN, Constants.DEFAULT_DELAY_BETWEEN_FAILED_LOGIN);
		}

		// Transfer mode
		String transferModePreference = mSharedPreferences.getString("transfer_mode_preference", null);
		if (transferModePreference != null) {
			bundle.putString(Constants.PREFERENCE_TRANSFER_MODE, transferModePreference);
		} else {
			bundle.putString(Constants.PREFERENCE_TRANSFER_MODE, Constants.DEFAULT_TRANSFER_MODE);
		}

		// Max simultaneous transfers
		String maxSimultaneousTransfersPreference = mSharedPreferences.getString("max_simultaneous_transfers_preference", null);
		if (maxSimultaneousTransfersPreference != null) {
			bundle.putInt(Constants.PREFERENCE_MAX_SIMULTANEOUS_TRANSFERS, Integer.parseInt(maxSimultaneousTransfersPreference));
		} else {
			bundle.putInt(Constants.PREFERENCE_MAX_SIMULTANEOUS_TRANSFERS, Constants.DEFAULT_MAX_SIMULTANEOUS_TRANSFERS);
		}
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

	/**
	 * 
	 * @return
	 */
	public void saveLastSelectedServer(long lastSelectedServer) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putLong("lastSelectedServer", lastSelectedServer);
		editor.commit();
	}

	/**
	 * 
	 * @return
	 */
	public long getLastSelectedServer() {
		return mSharedPreferences.getLong("lastSelectedServer", -1);
	}
}
