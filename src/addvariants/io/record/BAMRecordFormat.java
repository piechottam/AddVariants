package addvariants.io.record;

import addvariants.cli.parameters.Condition;
import addvariants.data.BaseQualRecordData;
import net.sf.samtools.SAMFileHeader;

public class BAMRecordFormat<T extends BaseQualRecordData> 
extends AbstractRecordFormat<T> {

	public static final char CHAR = 'B';
	public static final String DESCRIPTION = "BAM output";
	
	public BAMRecordFormat() {
		super(CHAR, DESCRIPTION);
	}

	@Override
	public BAMRecordWriter<T> createWriterInstance(final Condition<T> condition) {
		final String filename = condition.getRecordFilename();
		final SAMFileHeader header = condition.getSAMFileReader().get(0).getFileHeader();
		return new BAMRecordWriter<T>(filename, this, header);
	}
	
	@Override
	public String getSuffix() {
		return "bam";
	}

}