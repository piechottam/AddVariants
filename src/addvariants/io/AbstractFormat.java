package addvariants.io;

import addvariants.data.BaseQualRecordData;

/**
 * 
 * @author Michael Piechotta
 *
 */
public abstract class AbstractFormat<T extends BaseQualRecordData> {

	private char c;
	private String desc;

	public AbstractFormat(final char c, final String desc) {
		this.c = c;
		this.desc = desc;
	}

	public final char getC() {
		return c;
	}

	public final String getDesc() {
		return desc;
	}

	public abstract String getSuffix();
	
}
