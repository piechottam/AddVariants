package addvariants.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;

import addvariants.AddVariants;
import addvariants.cli.parameters.AbstractParameters;
import addvariants.cli.parameters.Condition;
import addvariants.data.BaseQualRecordData;
import addvariants.io.record.AbstractRecordWriter;
import addvariants.worker.Worker;

public class CopyTmp<T extends BaseQualRecordData> {

	private List<Integer> threadIds;
	
	private final AbstractParameters<T> parameters;
	private final List<Worker<T>> workerContainer;
	
	private final List<AbstractRecordWriter<T>> recordWriters;
	
	private final String variantFilename;
	private final BufferedWriter variantWriter;
	
	public CopyTmp(final List<Integer> threadIds, final AbstractParameters<T> parameters, final List<Worker<T>> workerContainer) throws IOException {
		this.threadIds = threadIds;
		this.parameters = parameters;
		this.workerContainer = workerContainer;
		
		// container for output 
		recordWriters = new ArrayList<AbstractRecordWriter<T>>(parameters.getConditions().size());
		
		// variants
		variantFilename = parameters.getVariantFilename();
		variantWriter = new BufferedWriter(new FileWriter(new File(variantFilename)));

		for (int conditionIndex = 0; conditionIndex < parameters.getConditions().size(); conditionIndex++) {
			final Condition<T> condition = parameters.getConditions().get(conditionIndex);
			// create record writer
			final AbstractRecordWriter<T> recordWriter = condition.getRecordFormat().createWriterInstance(condition);
			recordWriters.add(recordWriter);
		}
	}

	public void copy() throws IOException {
		AddVariants.getLogger().addInfo("Writing variants and records to output");
		int iteration = 0;
		for (int threadId : threadIds) {
			// current worker
			final Worker<T> worker = workerContainer.get(threadId);
			copyRecords(iteration, worker);
			copyVariants(iteration, worker);
			++iteration;
		}
	}

	private void copyRecords(final int iteration, final Worker<T> worker) {
		for (int conditionIndex = 0; conditionIndex < parameters.getConditions().size(); conditionIndex++) {
			copyRecords(iteration, conditionIndex, worker);
		}
	}
	
	private void copyRecords(final int iteration, final int conditionIndex, final Worker<T> worker) {
		final TmpWorkerWriter<T> tmpWriter = worker.getTmpWriter();
		// countes how many records where read from tmp
		int records = 0;
		// total processed record counts
		final List<Integer> totalProcessedRecords = tmpWriter.getCondition2Records().get(conditionIndex);
		// currently relevant processed counts
		final int processedRecords = totalProcessedRecords.get(iteration);
		
		final SAMRecordIterator recordIterator = worker.getTmpReader().getRecordIterator(conditionIndex);
		
		// switch when there are no more records
		// OR
		// when records from an other thread are needed...
		while (recordIterator.hasNext() && records <= processedRecords) {
			final SAMRecord record = recordIterator.next();
			final AbstractRecordWriter<T> w = recordWriters.get(conditionIndex);
			w.addRecord(record);
			++records;
		}
	} 
	
	private void copyVariants(final int iteration, final Worker<T> worker) throws IOException {
		int variants = 0;
		final BufferedReader br = worker.getTmpReader().getVariantReader();
		final int proccessedVariants = worker.getTmpWriter().getVariantCount().get(iteration);
		
		String line;
		while (proccessedVariants >= variants && (line = br.readLine()) != null) {
			variantWriter.write(line);
			variantWriter.newLine();
			variants++;
		}
	}
	
	public void close() throws IOException {
		for (final Worker<T> worker : workerContainer) {
			worker.getTmpReader().close();

			for (int conditionIndex = 0; conditionIndex < parameters.getConditions().size(); conditionIndex++) {
				recordWriters.get(conditionIndex).close();
			}
		}
		
		variantWriter.close();
	}

}
