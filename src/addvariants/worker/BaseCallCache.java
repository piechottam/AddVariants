package addvariants.worker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jacusa.data.BaseCallConfig;
import jacusa.util.Coordinate;

import net.sf.samtools.AlignmentBlock;
import net.sf.samtools.SAMRecord;

import addvariants.data.builder.SAMRecordWrapper;

public class BaseCallCache extends AbstractCache {

	private final BaseCallConfig baseCallConfig;
	
	// container for base calls and qualities scores
	private int[] coverage;
	private int[][] baseCalls;
	private int[][][] baseCallQualities;

	private List<List<SAMRecordWrapper>> records;

	public BaseCallCache(final BaseCallConfig baseCallConfig, final int activeWindowSize) {
		super(activeWindowSize);
		this.baseCallConfig = baseCallConfig;

		// how many bases will be considered
		final int bases = baseCallConfig.getBases().length;
		// range of base call quality score 
		final byte maxBQ = baseCallConfig.getMaxBQ();
		final byte minBQ = baseCallConfig.getMinBQ();

		coverage = new int[activeWindowSize];
		baseCalls = new int[bases][activeWindowSize];
		baseCallQualities = new int[bases][maxBQ - minBQ + 1][activeWindowSize];
		
		records = new ArrayList<List<SAMRecordWrapper>>(activeWindowSize);
		for (int i = 0; i < activeWindowSize; ++i) {
			records.add(new ArrayList<SAMRecordWrapper>(50));
		}
		
	}

	@Override
	protected void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		final SAMRecord record = recordWrapper.getSAMRecord();

		for (final AlignmentBlock block : record.getAlignmentBlocks()) {
			int referencePosition = block.getReferenceStart();
			int windowPosition = Coordinate.makeRelativePosition(getActiveWindowCoordinates(), referencePosition);
			int readPosition = block.getReadStart() - 1;

			// alignment length
			int length = block.getLength();
			
			if (windowPosition == -1) {
				windowPosition = referencePosition - getActiveWindowCoordinates().getStart();
				if (windowPosition > getActiveWindowSize()) { // downtstream of window -> ignore TODO distance
					continue;
				}
				// alignment outside of window - upstream TODO distance
				if (windowPosition + length < 0) { 
					continue;
				}
				final int offset = Math.abs(windowPosition); 
				windowPosition += offset;
				readPosition += offset;
				length -= offset;
			}

			int lengthOffset = getActiveWindowSize() - (windowPosition + length);
			if (lengthOffset <= 0) {
				incrementBaseCalls(windowPosition, readPosition, length + lengthOffset, recordWrapper);
				return;
			}
			incrementBaseCalls(windowPosition, readPosition, length, recordWrapper);
		}
	}
	protected void incrementBaseCalls(final int windowPosition, 
			final int readPosition, final int length, final SAMRecordWrapper recordWrapper) {
		final SAMRecord record = recordWrapper.getSAMRecord();
		for (int i = 0; i < length; ++i) {
			// ensure minimal base call quality score
			final byte baseCallQuality = record.getBaseQualities()[readPosition + i];
			if (baseCallQuality < baseCallConfig.getMinBQ()) {
				continue;
			}
			
			// consider only chosen bases
			final int baseIndex = baseCallConfig.getBaseIndex(record.getReadBases()[readPosition + i]);
			if (baseIndex < 0) {
				continue;
			}

			_incrementBaseCalls(windowPosition + i, baseIndex, baseCallQuality, recordWrapper);
		}
	}

	protected void _incrementBaseCalls(final int windowPosition, final int baseIndex, final byte baseCallQuality, 
			final SAMRecordWrapper recordWrapper) {
		// increment
		try {
			coverage[windowPosition] += 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		baseCalls[baseIndex][windowPosition] += 1;
		baseCallQualities[baseIndex][baseCallQuality - baseCallConfig.getMinBQ()][windowPosition] += 1;
		// and store recordWrapper reference
		records.get(windowPosition).add(recordWrapper);
	}
	
	protected boolean isValid(final int windowPosition) {
		return coverage[windowPosition] > 0;
	}

	private void init(final int activeWindowSize) {

	}
	
	@Override
	protected void clear() {
		if (coverage == null || coverage.length != getActiveWindowSize()) {
			init(getActiveWindowSize());
			return;
		}
		
		Arrays.fill(coverage, 0);
		for (int[] b : baseCalls) {
			Arrays.fill(b, 0);	
		}
		for (int[] b : baseCalls) {
			Arrays.fill(b, 0);	
		}
		for (int[][] bcs : baseCallQualities) {
			for (int[] b : bcs) {
				Arrays.fill(b, 0);	
			}
		}

		for (int i = 0; i < getActiveWindowSize(); ++i) {
			records.get(i).clear();
		}
	}

	public int getCoverage(final int windowPosition) {
		return coverage[windowPosition];
	}
	
	public int getBaseCalls(final int baseIndex, final int windowPosition) {
		return baseCalls[baseIndex][windowPosition];
	}
	
	public int getBaseCallQualities(final int baseIndex, final int baseQualIndex, final int windowPosition) {
		return baseCallQualities[baseIndex][baseQualIndex][windowPosition];
	}

	public List<SAMRecordWrapper> getRecordWrapper(final int windowPosition) {
		return records.get(windowPosition);
	}

}