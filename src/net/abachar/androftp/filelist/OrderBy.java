package net.abachar.androftp.filelist;

/**
 * 
 * @author abachar
 */
public enum OrderBy {
	NAME, TYPE, TIME, SIZE;

	public static OrderBy getByOrdinal(int ordinal) {
		switch (ordinal) {
			case 0:
				return NAME;
			case 1:
				return TYPE;
			case 2:
				return TIME;
			default: /* case 3: */
				return SIZE;
		}
	}
}
