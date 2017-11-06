package addvariants.io.variant;

import addvariants.data.BaseQualRecordData;

public class VCFVariantFormat<T extends BaseQualRecordData> 
extends AbstractVariantFormat<T> {

	public static final char CHAR = 'V';

	public VCFVariantFormat() {
		super(CHAR, "VCF variant output");
	}

	@Override
	public VCFVariantWriter<T> createWriterInstance(final String filename) {
		return new VCFVariantWriter<T>(filename, this);
	}
	
	@Override
	public String getSuffix() {
		return "vcf";
	}
}
