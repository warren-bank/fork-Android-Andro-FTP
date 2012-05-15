package net.abachar.androftp.util;

public interface Constants {

	/** Default values */
	int DEFAULT_TIMEOUT = 20;
	int DEFAULT_MAX_NBR_RETRIES = 2;
	int DEFAULT_DELAY_BETWEEN_FAILED_LOGIN = 5;
	String DEFAULT_TRANSFER_MODE = "P";
	int DEFAULT_MAX_SIMULTANEOUS_TRANSFERS = 2;

	/** Preferences */
	String PREFERENCE_TIMEOUT = "preference.timeout";
	String PREFERENCE_MAX_NBR_RETRIES = "preference.max_nbr_retries";
	String PREFERENCE_DELAY_BETWEEN_FAILED_LOGIN = "preference.delay_between_failed_login";
	String PREFERENCE_TRANSFER_MODE = "preference.transfer_mode";
	String PREFERENCE_MAX_SIMULTANEOUS_TRANSFERS = "preference.max_simultaneous_transfers";
}
