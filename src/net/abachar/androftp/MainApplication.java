package net.abachar.androftp;

/**
 * 
 * @author abachar
 */
public class MainApplication extends android.app.Application {

	/** Unique instance */
	private static MainApplication instance;

	/**
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
	}

	/**
	 * 
	 * @return
	 */
	public static MainApplication getInstance() {
		return instance;
	}
}
