package net.abachar.androftp.ui;

import net.abachar.androftp.R;
import net.abachar.androftp.ui.fragment.ConsoleFragment;
import net.abachar.androftp.ui.fragment.LocalManagerFragment;
import net.abachar.androftp.ui.fragment.ServerManagerFragment;
import net.abachar.androftp.ui.fragment.TransferFragment;
import android.app.Fragment;

/**
 * 
 * @author abachar
 */
public enum TabId {
	CONSOLE(ConsoleFragment.class, R.string.main_tab_console),
	LOCAL_MANAGER(LocalManagerFragment.class, R.string.main_tab_local), 
	SERVER_MANAGER(ServerManagerFragment.class, R.string.main_tab_server), 
	TRANSFER_MANAGER(TransferFragment.class, R.string.main_tab_transfers);

	/**
	 * 
	 */
	private final Class<? extends Fragment> clazz;

	/**
	 * 
	 */
	private final int textId;

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
