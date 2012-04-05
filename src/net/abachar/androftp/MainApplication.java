package net.abachar.androftp;

import android.app.Application;

/**
 * 
 * @author abachar
 */
public class MainApplication extends Application {

	/** Shared preferences file */
	public static final String SHARED_PREFERENCES_NAME = "AndroFTPSharedPreferences";

	/** Unique instance */
	private static MainApplication mInstance;

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
	 * @return
	 */
	public static MainApplication getInstance() {
		return mInstance;
	}
}
