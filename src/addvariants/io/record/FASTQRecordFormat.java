package addvariants.io.record;

import addvariants.cli.parameters.Condition;
import addvariants.data.BaseQualRecordData;

public class FASTQRecordFormat<T extends BaseQualRecordData> 
extends AbstractRecordFormat<T> {

	public static final char CHAR = 'F';

	public FASTQRecordFormat() {
		super(CHAR, "FASTQ output");
	}
	
	@Override
	public AbstractRecordWriter<T> createWriterInstance(Condition<T> condition) {
		return new FASTQRecordWriter<T>(condition.getRecordFilename(), this);
	}

	@Override
	public String getSuffix() {
		return "fastq";
	}
	
}
