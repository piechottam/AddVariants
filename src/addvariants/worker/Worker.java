package addvariants.worker;

import jacusa.util.Coordinate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import addvariants.AddVariants;
import addvariants.cli.parameters.Condition;
import addvariants.data.BaseQualRecordData;
import addvariants.data.builder.SAMRecordWrapper;
import addvariants.data.builder.SAMRecordWrapperProvider;
import addvariants.dispatcher.WorkerDispatcher;
import addvariants.io.TmpWorkerReader;
import addvariants.io.TmpWorkerWriter;
import addvariants.io.variant.AbstractVariantFormat;
import addvariants.utils.Variant;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMFileWriter;

/**
 * 
 * @author Michael Piechotta
 *
 * 
 */
public class Worker<T extends BaseQualRecordData> 
extends Thread {

	public static enum STATUS {INIT, READY, FINISHED, BUSY, WAITING};

	private final WorkerDispatcher<T> workerDispatcher;
	private final ThreadIdContainer threadIdContainer;
	private STATUS status;
	
	private final List<SAMRecordWrapperProvider> recordProviders;

	private final TmpWorkerWriter<T> tmpWriter;
	private TmpWorkerReader<T> tmpReader;

	private final SAMRecordModifier recordModifier;
	
	private CoordinateController coordinateController;
	private final List<OverlappingRecordWrapperContainer> windowContainers;
	
	public Worker(final WorkerDispatcher<T> workerDispatcher, 
			final int threadId, final AbstractVariantFormat<T> variantFormat, 
			final SAMRecordModifier recordModifier) throws IOException {
		this.workerDispatcher = workerDispatcher;
		threadIdContainer = new ThreadIdContainer(threadId);
		status = STATUS.INIT;
		
		recordProviders = createRecordProviders(threadId, getConditions());
		
		tmpWriter = new TmpWorkerWriter<T>(threadId, getConditions(), variantFormat);
		this.recordModifier = recordModifier;

		windowContainers = createOverlappingContainers(getConditions().size());
	}

	private List<OverlappingRecordWrapperContainer> createOverlappingContainers(final int conditions) {
		final List<OverlappingRecordWrapperContainer> container = new ArrayList<OverlappingRecordWrapperContainer>(conditions);
		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			container.add(new OverlappingRecordWrapperContainer());
		}
		return container;
	}
	
	private List<SAMRecordWrapperProvider> createRecordProviders(final int threadId, final List<Condition<T>> conditions) {
		List<SAMRecordWrapperProvider> recordProvider = new ArrayList<SAMRecordWrapperProvider>(getConditions().size());
		for (final Condition<T> condition : conditions) {
			final SAMFileReader reader = condition.createSAMFileReader();
			final SAMRecordWrapperProvider provider = new SAMRecordWrapperProvider(reader, condition);
			recordProvider.add(provider);
		}
		return recordProvider;
	}

	private List<Condition<T>> getConditions() {
		return workerDispatcher.getMethodFactory().getParameters().getConditions();
	}
	
	private int getActiveWindowSize() {
		return workerDispatcher.getMethodFactory().getParameters().getActiveWindowSize();
	}
	
	public synchronized void updateReservedWindowCoordinate(final Coordinate reservedWindowCoordinate) {
		coordinateController = new CoordinateController(reservedWindowCoordinate, getActiveWindowSize());
	}
	
	@Override
	public final void run() {
		while (status != STATUS.FINISHED) {
			switch (status) {

			case WAITING:
				// TODO
				break;
			
			case READY:
				synchronized (this) {
					status = STATUS.BUSY;
					processRecordModifier();
					status = STATUS.INIT; // TODO what status
				}
				break;
				
			case INIT:
				Coordinate reserverdWindowCoordinate = null;
				synchronized (workerDispatcher) {
					if (workerDispatcher.hasNext()) {
						if (workerDispatcher.getThreadIds().size() > 0) {
							int n = workerDispatcher.getThreadIds().size();
							final int previous = workerDispatcher.getThreadIds().get(n - 1);
							// TODO
						}
						workerDispatcher.getThreadIds().add(threadIdContainer.getThreadId());
						reserverdWindowCoordinate = workerDispatcher.next();
					}
				}
				synchronized (this) {
					if (reserverdWindowCoordinate == null) {
						setStatus(STATUS.FINISHED);
					} else {
						updateReservedWindowCoordinate(reserverdWindowCoordinate);
						setStatus(STATUS.READY);
					}
				}
				break;

			
			default:
				break;
			}
		}

		tmpWriter.close();
		try {
			tmpReader = new TmpWorkerReader<T>(tmpWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		synchronized (workerDispatcher) {
			workerDispatcher.notify();
		}

	}

	private int processLeft(final int[] recordCount) {
		int variantCount = 0;
		
		return variantCount;
	}
	
	private void processRecordModifier() {
		AddVariants.getLogger().addInfo("Thread " + (threadIdContainer.getThreadId() + 1) + ": " +
				"Implanting variants to contig " + 
				coordinateController.getReserved().getContig() + ":" + 
				coordinateController.getReserved().getStart() + "-" + 
				coordinateController.getReserved().getEnd());
		
		// counter to reconstruct order from tmp writers
		final int[] recordCount = new int[getConditions().size()];
		int variantCount = 0;
					
		while (coordinateController.hasNext()) {
			// get next active window within reserved
			final Coordinate active = coordinateController.next();
			// get iterator for SAMRecords within active window 
			final List<Iterator<SAMRecordWrapper>> iterators = createIterators(active, coordinateController.getReserved());
			final List<List<SAMRecordWrapper>> readRecords = recordModifier.build(active, iterators);
			
			// mutate and write variants - SAMRecords in readRecords might be changed
			variantCount += createAndWriteVariants();

			// write records
			writeRecords(readRecords, recordCount);

			// clear overlapping container(s)
			clear();
		}		
		
		tmpWriter.updateCounts(variantCount, recordCount);
	}

	private int createAndWriteVariants() {
		int variantCount = 0;
		while (recordModifier.hasNext()) {
			Variant[] variants = recordModifier.next();
			try {
				tmpWriter.getVariantWriter().addVariants(variants, getConditions());
			} catch (Exception e) {
				e.printStackTrace();
			}
			variantCount++;
		}
		return variantCount;
	}

	private void writeRecords(final List<List<SAMRecordWrapper>> readRecords, final int[] recordCount) {
		for (int conditionIndex = 0; conditionIndex < getConditions().size(); conditionIndex++) {
			final SAMFileWriter tmpRecordWriter = tmpWriter.getRecordWriter(conditionIndex);
			for (final SAMRecordWrapper recordWrapper : readRecords.get(conditionIndex)) {
				if (! recordWrapper.overlapsWindowBorders()) {
					tmpRecordWriter.addAlignment(recordWrapper.getSAMRecord());
					recordWrapper.setPrinted();
					recordCount[conditionIndex]++;
				}
			}
		}
	}
	
	private void clear() {
		/*
		if (coordinateController.isLeft()) {

		}
		*/

		if (coordinateController.isInner()) {
			for (int conditionIndex = 0; conditionIndex < getConditions().size(); conditionIndex++) {
				windowContainers.get(conditionIndex).getLeft().clear();
			}		
		}
		
		if (coordinateController.isRight()) {
			for (int conditionIndex = 0; conditionIndex < getConditions().size(); conditionIndex++) {
				windowContainers.get(conditionIndex).clear();
			}
		}
	}
	
	private boolean hasLeft() {
		for (final OverlappingRecordWrapperContainer container : windowContainers) {
			if (! container.getLeft().isEmpty()) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean hasRight() {
		for (final OverlappingRecordWrapperContainer container : windowContainers) {
			if (! container.getRight().isEmpty()) {
				return true;
			}
		}

		return false;
	}
	
	// TODO keep track of closeable iterator
	private List<Iterator<SAMRecordWrapper>> createIterators(final Coordinate activeWindowCoordinate, final Coordinate reserverdWindowCoordinate) {
		final List<Iterator<SAMRecordWrapper>> iterators = 
				new ArrayList<Iterator<SAMRecordWrapper>>(getConditions().size());
		if (coordinateController.isLeft()) {
			// TODO left outer already computed
			for (int conditionIndex = 0; conditionIndex < getConditions().size(); conditionIndex++) {
				final SAMRecordWrapperIterator iterator = recordProviders.get(conditionIndex).getIterator(activeWindowCoordinate, reserverdWindowCoordinate);
				iterators.add(iterator);
			}
		}

		if (coordinateController.isInner()) {
			for (int conditionIndex = 0; conditionIndex < getConditions().size(); conditionIndex++) {
				final List<Iterator<SAMRecordWrapper>> tmpIterators = new ArrayList<Iterator<SAMRecordWrapper>>(2);
				// get it to reads that overlap active window on left site
				tmpIterators.add(getWindowContainer().get(conditionIndex).getLeft().iterator());
				// get it to reads that are within this active window ( ] - overlapping right side of window
				tmpIterators.add(recordProviders.get(conditionIndex).getIterator(activeWindowCoordinate, reserverdWindowCoordinate));

				final Iterator<SAMRecordWrapper> iterator = new CombinedSAMRecordWrapperIterator(tmpIterators);
				iterators.add(iterator);
			}			
		}
		
		if (coordinateController.isRight()) {
			for (int conditionIndex = 0; conditionIndex < getConditions().size(); conditionIndex++) {
				final List<Iterator<SAMRecordWrapper>> tmpIterators = new ArrayList<Iterator<SAMRecordWrapper>>(2);
				// get it to reads that overlap active window on left site
				tmpIterators.add(getWindowContainer().get(conditionIndex).getLeft().iterator());
				// get it to reads that are within this active window ( ] - overlapping right side of window
				tmpIterators.add(recordProviders.get(conditionIndex).getIterator(activeWindowCoordinate, reserverdWindowCoordinate));
				// get it to reads that overlap active window on right site
				tmpIterators.add(getWindowContainer().get(conditionIndex).getLeft().iterator());

				final Iterator<SAMRecordWrapper> iterator = new CombinedSAMRecordWrapperIterator(tmpIterators);
				iterators.add(iterator);
			}			
		}

		return iterators;
	}
	
	public STATUS getStatus() {
		return status;
	}

	public void setStatus(STATUS status) {
		this.status = status;
	}
	
	public ThreadIdContainer getThreadIdContainer() {
		return threadIdContainer;
	}

	public TmpWorkerReader<T> getTmpReader() {
		return tmpReader;
	}
	
	public TmpWorkerWriter<T> getTmpWriter() {
		return tmpWriter;
	}

	public List<OverlappingRecordWrapperContainer> getWindowContainer() {
		return windowContainers;
	}

	/*
	if (isInnerWindow()) {
		
	} else if (isLeftWindow()) {
		
	} else if (isRightWindow()) {
		
	} else {
		throw new IllegalStateException(); // TODO add text
	}
	*/

}
