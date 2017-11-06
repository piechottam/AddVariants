package addvariants.io.record;

import java.io.File;
import java.util.List;

import addvariants.cli.parameters.Condition;
import addvariants.data.BaseQualRecordData;
import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMFileWriter;

public class BAMRecordWriter<T extends BaseQualRecordData> 
extends AbstractRecordFactoryWriter<T> {
	
	public BAMRecordWriter(final String filename, final AbstractRecordFormat<T> format, 
			final SAMFileHeader header) {
		super(filename, format, header);
		final File file = new File(filename);
		final SAMFileWriter writer = getFactory().makeBAMWriter(header, true, file);
		setWriter(writer);
	}

	@Override
	public void addHeader(List<Condition<T>> conditions) { 
		// handled elsewhere...
	}
	
}
