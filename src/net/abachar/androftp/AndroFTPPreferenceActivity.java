package net.abachar.androftp;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * 
 * @author abachar
 */
public class AndroFTPPreferenceActivity extends PreferenceActivity {

	/**
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load preferences
		addPreferencesFromResource(R.xml.preferences);
	}
}
