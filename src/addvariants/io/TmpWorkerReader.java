package addvariants.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.zip.GZIPInputStream;

import addvariants.cli.parameters.Condition;
import addvariants.data.BaseQualRecordData;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecordIterator;

public class TmpWorkerReader<T extends BaseQualRecordData> {

	private final File[] recordFiles;
	private final SAMFileReader[] recordReaders;
	private final SAMRecordIterator[] recordIterator;
	
	private final File variantFile;
	private final BufferedReader variantReader; 
	
	public TmpWorkerReader(final TmpWorkerWriter<T> workerWriter) throws IOException {
		final List<Condition<T>> conditions = workerWriter.getConditions();
		recordFiles = new File[conditions.size()];
		recordReaders = new SAMFileReader[conditions.size()];
		recordIterator = new SAMRecordIterator[conditions.size()];
		
		// init record readers
		for (int conditionIndex = 0; conditionIndex < conditions.size(); conditionIndex++) {
			final String recordFilename = workerWriter.getRecordTmpFilename(conditionIndex);
			final File recordFile = new File(recordFilename);
			recordFiles[conditionIndex] = recordFile;

			final FileInputStream stream = new FileInputStream(recordFile);
			// SAM files are zipped
			final GZIPInputStream zip = new GZIPInputStream(stream);
			recordReaders[conditionIndex] = new SAMFileReader(zip);
			// iterator over records in tmp files
			final SAMFileReader reader = recordReaders[conditionIndex];
			recordIterator[conditionIndex] = reader.iterator();
		}
		
		// init variant reader
		final String variantFilename = workerWriter.getVariantTmpFilename();
		variantFile = new File(variantFilename);
		variantReader = new BufferedReader(new FileReader(variantFile));	
	}
	
	public SAMRecordIterator getRecordIterator(final int conditionIndex) {
		return recordIterator[conditionIndex];
	}
	
	public BufferedReader getVariantReader() {
		return variantReader;
	}
	
	public void close() {
		// TODO
	}
	
}
