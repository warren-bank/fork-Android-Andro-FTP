package net.abachar.androftp.filelist.manager;

import java.util.ArrayList;
import java.util.List;

public class BackgroundOperationResult {

	private boolean mSuccess;
	private List<FileManagerEvent> mEvents = new ArrayList<FileManagerEvent>();

	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return mSuccess;
	}

	/**
	 * @param mSuccess
	 *            the mSuccess to set
	 */
	public void setSuccess(boolean success) {
		this.mSuccess = success;
	}

	/**
	 * @return the mEvents
	 */
	public List<FileManagerEvent> getEvents() {
		return mEvents;
	}

	/**
	 * @param mEvents
	 *            the event to set
	 */
	public void addEvent(FileManagerEvent event) {
		mEvents.add(event);
	}
}
