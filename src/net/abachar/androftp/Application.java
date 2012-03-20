package net.abachar.androftp;

/**
 * 
 * @author abachar
 */
public class Application extends android.app.Application {

	/** */
	private static Application instance;

	/**
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		instance.initializeInstance();
	}

	/**
	 * 
	 * @return
	 */
	public static Application getInstance() {
		return instance;
	}

	/**
	 * 
	 */
	protected void initializeInstance() {
	}
}
