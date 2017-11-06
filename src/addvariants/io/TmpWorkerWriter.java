package addvariants.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMFileWriter;
import net.sf.samtools.SAMFileWriterFactory;
import addvariants.cli.parameters.Condition;
import addvariants.data.BaseQualRecordData;
import addvariants.io.variant.AbstractVariantFormat;
import addvariants.io.variant.AbstractVariantWriter;

public class TmpWorkerWriter<T extends BaseQualRecordData> {

	private final int threadId;
	private List<Condition<T>> conditions;
	
	private final List<File> files;

	private SAMFileWriterFactory factory;
	private final String[] recordFilename;
	private final SAMFileWriter[] recordWriters;
	private final List<List<Integer>> condition2recordCount;

	private final String variantFilename;
	private final AbstractVariantWriter<T> variantWriter;
	private final List<Integer> variantCount;

	public TmpWorkerWriter(final int threadId,
			final List<Condition<T>> conditions,
			final AbstractVariantFormat<T> variantFormat) throws IOException {
		this.threadId = threadId;
		this.conditions = conditions;

		files = new ArrayList<File>(conditions.size() + 1);
		recordFilename = new String[conditions.size()];
		variantFilename = createVariantTmpFilename();

		// init counters
		condition2recordCount = new ArrayList<List<Integer>>(conditions.size());
		variantCount  = new ArrayList<Integer>(1000);

		// init tmp writer for records and variants
		factory = new SAMFileWriterFactory();
		recordWriters 	= new SAMFileWriter[conditions.size()];
		variantWriter 	= variantFormat.createWriterInstance(variantFilename);

		for (int conditionIndex = 0; conditionIndex < conditions.size(); ++conditionIndex) {
			final Condition<T> condition = conditions.get(conditionIndex);
			// populate
			condition2recordCount.add(new ArrayList<Integer>(10000));

			recordFilename[conditionIndex] 	= createRecordTmpFilename(conditionIndex);
			final SAMFileHeader header 		= condition.getSAMFileReader().get(0).getFileHeader();
			try {
				final GZIPOutputStream tmpRecordStream 	= new GZIPOutputStream(new FileOutputStream(recordFilename[conditionIndex]), 10000);
				recordWriters[conditionIndex] = factory.makeSAMWriter(header, true, tmpRecordStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void updateCounts(final int variants, final int[] records) {
		for (int conditionIndex = 0; conditionIndex < conditions.size(); conditionIndex++) {
			variantCount.add(variants);
			condition2recordCount.get(conditionIndex).add(records[conditionIndex]);
		}
	}
	
	public List<List<Integer>> getCondition2Records() {
		return condition2recordCount;
	}

	public List<Integer> getVariantCount() {
		return variantCount;
	}

	public SAMFileWriter getRecordWriter(final int conditionIndex) {
		return recordWriters[conditionIndex];
	}
	
	public AbstractVariantWriter<T> getVariantWriter() {
		return variantWriter;
	}
	
	public void close() {
		for (final SAMFileWriter writer : this.recordWriters) {
			writer.close();
		}
		variantWriter.close();
	}

	public String getRecordTmpFilename(final int conditionIndex) {
		return recordFilename[conditionIndex];
	}
	
	public String getVariantTmpFilename() {
		return variantFilename;
	}
	
	public List<Condition<T>> getConditions() {
		return conditions;
	}
	
	private String createRecordTmpFilename(final int conditionIndex) throws IOException {
		final Condition<T> condition = conditions.get(conditionIndex);
		String prefix = condition.getRecordFilename() + "_" + threadId + "_" + conditionIndex;
		final File file = File.createTempFile(prefix, ".sam.gz");
		files.add(file);
		// TODO debug
		file.deleteOnExit();
		return file.getCanonicalPath();
	}

	private String createVariantTmpFilename() throws IOException {
		final String prefix = "variant_" + threadId + "_";
		final File file = File.createTempFile(prefix, ".gz");
		files.add(file);
		// TODO debug
		file.deleteOnExit();
		return file.getCanonicalPath();
	}
	
}
