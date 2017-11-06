package addvariants.io.variant;

import addvariants.data.BaseQualRecordData;
import addvariants.io.AbstractFormat;

public abstract class AbstractVariantFormat<T extends BaseQualRecordData> 
extends AbstractFormat<T> {

	public AbstractVariantFormat(final char c, final String desc) {
		super(c, desc);
	}

	public abstract AbstractVariantWriter<T> createWriterInstance(final String filename);

}
