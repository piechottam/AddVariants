package addvariants.data.builder;

import jacusa.util.Coordinate;

import addvariants.AddVariants;
import addvariants.cli.parameters.Condition;
import addvariants.worker.SAMRecordWrapperIterator;

import net.sf.samtools.SAMFileReader;

import net.sf.samtools.SAMRecordIterator;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class SAMRecordWrapperProvider {

	private final SAMFileReader reader;
	private int filteredSAMRecords;
	private int SAMRecords;

	private final Condition<?> condition;

	public SAMRecordWrapperProvider (
			final SAMFileReader reader, 
			final Condition<?> condition) {
		this.reader			= reader;

		filteredSAMRecords	= 0;
		SAMRecords			= 0;

		this.condition		= condition;
	}

	// get iterator to fill the window
	public SAMRecordWrapperIterator getIterator(final Coordinate activeWindowCoordinate, final Coordinate reservedWindowCoordinate) {
		final SAMRecordIterator iterator = createSAMRecordIterator(reservedWindowCoordinate);
		return new SAMRecordWrapperIterator(this, activeWindowCoordinate, iterator);
	}

	private SAMRecordIterator createSAMRecordIterator(final Coordinate windowCoordinate) {
		check(windowCoordinate);

		return reader.query(
				windowCoordinate.getContig(), 
				windowCoordinate.getStart(), 
				windowCoordinate.getEnd(), 
				false);		
	}

	private void check(final Coordinate windowCoordinate) {
		final int sequenceLength = reader
				.getFileHeader()
				.getSequence(windowCoordinate.getContig())
				.getSequenceLength();
	
		if (windowCoordinate.getEnd() > sequenceLength) {
			Coordinate samHeader = new Coordinate(windowCoordinate.getContig(), 1, sequenceLength);
			AddVariants.getLogger().addWarning("Coordinates in BED file (" + windowCoordinate.toString() + 
					") exceed SAM sequence header (" + samHeader.toString()+ ").");
		}
	}

	public int getFilteredSAMRecords() {
		return filteredSAMRecords;
	}

	public int getSAMRecords() {
		return SAMRecords;
	}

	final public void incrementFilteredSAMRecords() {
		filteredSAMRecords++;
	}

	final public void incrementSAMRecords() {
		SAMRecords++;
	}
	
	public Condition<?> getCondition() {
		return condition;
	}
		
}
