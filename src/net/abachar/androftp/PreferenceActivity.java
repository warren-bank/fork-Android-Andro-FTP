package net.abachar.androftp;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.widget.Toast;

/**
 * 
 * @author abachar
 */
public class PreferenceActivity extends android.preference.PreferenceActivity {

	/**
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load preferences
		addPreferencesFromResource(R.xml.preferences);

		// Number validators
		PreferenceScreen ps = getPreferenceScreen();
		ps.findPreference("timeout_preference").setOnPreferenceChangeListener(numberCheckListener);
		ps.findPreference("max_nbr_retries_preference").setOnPreferenceChangeListener(numberCheckListener);
		ps.findPreference("delay_between_failed_login_preference").setOnPreferenceChangeListener(numberCheckListener);

		// Maximum simultaneous transfers validator
		ps.findPreference("max_simultaneous_transfers_preference").setOnPreferenceChangeListener(maxSimultaneousTransfersCheckListener);
	}

	/**
	 * 
	 * @param newValue
	 * @return
	 */
	private boolean validateNamber(String newValue) {

		// Not null and it's a number
		if ("".equals(newValue) || !newValue.matches("^\\d+$")) {
			return false;
		}

		// > 0
		return Integer.parseInt(newValue) > 0;
	}

	/**
	 * Checks that a preference is a valid numerical value
	 */
	Preference.OnPreferenceChangeListener numberCheckListener = new Preference.OnPreferenceChangeListener() {

		/**
		 * @see android.preference.Preference.OnPreferenceChangeListener#onPreferenceChange(android.preference.Preference,
		 *      java.lang.Object)
		 */
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {

			boolean bRet = validateNamber(newValue.toString());
			if (!bRet) {
				Toast.makeText(PreferenceActivity.this, getString(R.string.err_value_must_be_integer), Toast.LENGTH_SHORT).show();
			}
			return bRet;
		}
	};

	/**
	 * Checks that a preference is a valid numerical value
	 */
	Preference.OnPreferenceChangeListener maxSimultaneousTransfersCheckListener = new Preference.OnPreferenceChangeListener() {

		/**
		 * @see android.preference.Preference.OnPreferenceChangeListener#onPreferenceChange(android.preference.Preference,
		 *      java.lang.Object)
		 */
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {

			boolean bRet = validateNamber(newValue.toString());
			if (!bRet) {
				Toast.makeText(PreferenceActivity.this, getString(R.string.err_value_must_be_integer), Toast.LENGTH_SHORT).show();
				return false;
			}

			int i = Integer.parseInt(newValue.toString());
			bRet = (i > 0) && (i <= 5);
			if (!bRet) {
				Toast.makeText(PreferenceActivity.this, getString(R.string.err_value_must_be_integer_between_1_5), Toast.LENGTH_SHORT).show();
			}

			return bRet;
		}
	};
}