package addvariants.io.record;

import addvariants.cli.parameters.Condition;
import addvariants.data.BaseQualRecordData;
import net.sf.samtools.SAMFileHeader;

public class SAMRecordFormat<T extends BaseQualRecordData> 
extends AbstractRecordFormat<T> {

	public static final char CHAR = 'S';
	
	public SAMRecordFormat() {
		super(CHAR, "SAM output");
	}
	
	@Override
	public SAMRecordWriter<T> createWriterInstance(Condition<T> condition) {
		final String filename = condition.getRecordFilename();
		final SAMFileHeader header = condition.getSAMFileReader().get(0).getFileHeader();
		return new SAMRecordWriter<T>(filename, this, header);
	}

	@Override
	public String getSuffix() {
		return "sam";
	}
	
}