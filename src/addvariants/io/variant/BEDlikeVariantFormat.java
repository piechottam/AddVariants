package addvariants.io.variant;

import addvariants.data.BaseQualRecordData;

public class BEDlikeVariantFormat<T extends BaseQualRecordData> 
extends AbstractVariantFormat<T> {

	public static final char CHAR = 'B';

	public BEDlikeVariantFormat() {
		super(CHAR, "BEDlike variant output");
	}

	@Override
	public BEDlikeVariantWriter<T> createWriterInstance(final String filename) {
		return new BEDlikeVariantWriter<T>(filename, this);
	}

	@Override
	public String getSuffix() {
		return "bed";
	}
	
}
