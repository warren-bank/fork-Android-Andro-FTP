package net.abachar.androftp.filelist.manager;

import java.util.ArrayList;
import java.util.List;

public class BackgroundOperationResult {

	private boolean mSuccess;
	private List<FileManagerEvent> mReplacementEvents = new ArrayList<FileManagerEvent>();

	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return mSuccess;
	}

	/**
	 * @param success
	 *            the success to set
	 */
	public void setSuccess(boolean success) {
		this.mSuccess = success;
	}

	/**
	 * @return the replacementEvents
	 */
	public List<FileManagerEvent> getReplacementEvents() {
		return mReplacementEvents;
	}

	/**
	 * @param mReplacementEvents
	 *            the replacementEvents to set
	 */
	public void addReplacementEvent(FileManagerEvent event) {
		mReplacementEvents.add(event);
	}
}
