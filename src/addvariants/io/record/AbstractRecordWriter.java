package addvariants.io.record;

import net.sf.samtools.SAMRecord;
import addvariants.data.BaseQualRecordData;
import addvariants.io.AbstractWriter;

public abstract class AbstractRecordWriter<T extends BaseQualRecordData> 
extends AbstractWriter<T> {
	
	public AbstractRecordWriter(final String filename, final AbstractRecordFormat<T> format) {
		super(filename, format);
	}
	
	public abstract void addRecord(final SAMRecord record);
	
}
