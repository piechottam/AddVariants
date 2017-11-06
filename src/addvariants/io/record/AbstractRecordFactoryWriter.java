package addvariants.io.record;

import addvariants.data.BaseQualRecordData;
import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMFileWriter;
import net.sf.samtools.SAMFileWriterFactory;
import net.sf.samtools.SAMRecord;

public abstract class AbstractRecordFactoryWriter<T extends BaseQualRecordData> 
extends AbstractRecordWriter<T> {

	private SAMFileHeader header;
	private SAMFileWriterFactory factory;
	private SAMFileWriter writer;
	
	public AbstractRecordFactoryWriter(final String filename, final AbstractRecordFormat<T> format,
			final SAMFileHeader header) {
		super(filename, format);

		this.header = header;
		factory = new SAMFileWriterFactory();
	}

	protected void setWriter(final SAMFileWriter writer) {
		this.writer = writer;
	}
	
	protected SAMFileWriterFactory getFactory() {
		return factory;
	}

	protected SAMFileHeader getHeader() {
		return header;
	}
	
	protected SAMFileWriter getWriter() {
		return writer;
	}
	
	public void addRecord(final SAMRecord record) {
		writer.addAlignment(record);
	}

	@Override
	public void close() {
		writer.close();
	}
	
}
