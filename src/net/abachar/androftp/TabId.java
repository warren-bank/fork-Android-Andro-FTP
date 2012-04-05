package net.abachar.androftp;

import net.abachar.androftp.filelist.LocalManagerFragment;
import net.abachar.androftp.filelist.ServerManagerFragment;
import net.abachar.androftp.transfers.TransferFragment;
import android.app.Fragment;

/**
 * 
 * @author abachar
 */
public enum TabId {

	/** Local file manager fragment */
	LOCAL_MANAGER(LocalManagerFragment.class, R.string.main_tab_local),

	/** Server file manager fragment */
	SERVER_MANAGER(ServerManagerFragment.class, R.string.main_tab_server),

	/** Transfers fragment */
	TRANSFER_MANAGER(TransferFragment.class, R.string.main_tab_transfers);

	/**
	 * 
	 */
	private final Class<? extends Fragment> clazz;

	/**
	 * 
	 */
	private final int textId;

	/**
	 * 
	 * @param clazz
	 * @param textId
	 */
	private TabId(Class<? extends Fragment> clazz, int textId) {
		this.clazz = clazz;
		this.textId = textId;
	}

	/**
	 * @return the clazz
	 */
	public Class<? extends Fragment> getClazz() {
		return clazz;
	}

	/**
	 * @return the textId
	 */
	public int getTextId() {
		return textId;
	}
}
