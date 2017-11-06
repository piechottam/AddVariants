package addvariants.worker;

import jacusa.data.BaseCallConfig;
import jacusa.util.Coordinate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import addvariants.cli.parameters.Condition;
import addvariants.cli.parameters.RandomMutationsParameters;
import addvariants.data.BaseQualRecordData;
import addvariants.data.builder.SAMRecordWrapper;
import addvariants.utils.ParallelData;
import addvariants.utils.Variant;

public class RandomMutation<T extends BaseQualRecordData> 
implements SAMRecordModifier {

	private Random random;

	private RandomMutationsParameters<T> parameters;
	private ParallelData<T> parallelData;

	private final BaseCallCache[] caches;
	private final List<List<SAMRecordWrapper>> readRecords; 

	private int windowPosition;
	private String activeWindowContig;
	private int activeWindowStart;
	private Variant[] currentVariants;
	
	public RandomMutation(final RandomMutationsParameters<T> parameters) {
		// TODO random = new Random(System.currentTimeMillis());
		random = new Random(10);
		
		this.parameters = parameters;
		parallelData = new ParallelData<T>(parameters.getMethodFactory());

		caches = new BaseCallCache[parameters.getConditions().size()];
		readRecords = new ArrayList<List<SAMRecordWrapper>>(parameters.getConditions().size());
		for (int conditionIndex = 0; conditionIndex < parameters.getConditions().size(); conditionIndex++) {
			final List<SAMRecordWrapper> list = new ArrayList<SAMRecordWrapper>(parameters.getActiveWindowSize());
			readRecords.add(list);
			final BaseCallCache cache = new BaseCallCache(parameters.getBaseConfig(), parameters.getActiveWindowSize());
			caches[conditionIndex] = cache;
		}
	}

	@Override
	public void addInfo(final SAMRecordWrapper recordWrapper) {
		recordWrapper.setMutated();
		// TODO position
	}
	
	@Override
	public String getParameterInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("mutatitionRate=");
		sb.append(parameters.getMutationRate());
		return sb.toString();
	}

	@Override
	public List<List<SAMRecordWrapper>> build(final Coordinate activeWindowCoordinate, 
			final List<Iterator<SAMRecordWrapper>> iterators) {
		
		for (int conditionIndex = 0; conditionIndex < parameters.getConditions().size(); conditionIndex++) {
			// reset
			final BaseCallCache cache = caches[conditionIndex]; 
			cache.setWindowCoordinates(activeWindowCoordinate);
			readRecords.get(conditionIndex).clear();

			final Iterator<SAMRecordWrapper> iterator = iterators.get(conditionIndex);
			while (iterator.hasNext()) {
				final SAMRecordWrapper recordWrapper = iterator.next(); 
				cache.addRecordWrapper(recordWrapper);
				readRecords.get(conditionIndex).add(recordWrapper);
			}
		}
		// TODO set windowPosition to covered
		windowPosition 		= 0;
		activeWindowContig 	= activeWindowCoordinate.getContig();
		activeWindowStart 	= activeWindowCoordinate.getStart();

		return readRecords;
	}

	@Override
	public boolean hasNext() {
		while (currentVariants == null && windowPosition != -1) {
			int check = 0;	
			for (final BaseCallCache cache : caches) {
				final int next = cache.getNext(windowPosition);
				if (next == -1) {
					return false;
				}

				if (windowPosition == cache.getNext(windowPosition)) {
					check++;
				}
			}
				
			if (check == caches.length && isValid(windowPosition)) {
				if (mutate()) {
					currentVariants = mutate(windowPosition);
					return true;
				}
			}

			windowPosition++;
		}

		return false;
	}
	
	@Override
	public Variant[] next() {
		final Variant[] tmp = currentVariants;
		windowPosition++;
		currentVariants = null;
		return tmp;
	}

	private boolean isValid(final int windowPosition) {
		for (int conditionIndex = 0; conditionIndex < parameters.getConditions().size(); conditionIndex++) {
			if (! isCovered(conditionIndex, windowPosition)) {
				return false;
			}
		}

		parallelData = updateParallelData(windowPosition);

		final char referenceBase = parallelData.getCombinedData().getReferenceBase();
		final int[] allelesIndexs = parallelData.getCombinedData().getBaseQualCount().getAlleles();
		Set<Character> bases = new HashSet<Character>();
		for (final char base : BaseCallConfig.getInstance().int2byte(allelesIndexs)) {
			bases.add(base);
		}

		// mutate only if sequenced bases != reference
		if (allelesIndexs.length > 1 || bases.contains(referenceBase)) {
			return false;
		}

		return true;
	}

	private boolean isCovered(final int conditionIndex, final int windowPosition) {
		final Condition<T> condition = parameters.getConditions().get(conditionIndex);
		final int minCoverage = condition.getMinCoverage();
		final int coverage = caches[conditionIndex].getCoverage(windowPosition);
		return coverage >= minCoverage;
	}

	private Variant[] mutate(final int windowPosition) {
		final int conditions = parallelData.getConditions();
		final Variant[] variants = new Variant[conditions];
		
		final char referenceBase = parallelData.getCombinedData().getReferenceBase();
		final char mutatedBase = mutateBase(referenceBase);
		
		final int referencePosition = activeWindowStart + windowPosition;

		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			final T data = parallelData.getData(conditionIndex);

			for (final SAMRecordWrapper recordWrapper : data.getRecordWrapper()) {
				final byte[] baseCalls = recordWrapper.getSAMRecord().getReadBases();
				final int readPosition = recordWrapper.getReadPositionAtReferencePosition(referencePosition);
				baseCalls[readPosition] = (byte)mutatedBase;
				recordWrapper.getSAMRecord().setReadBases(baseCalls);

				// TODO move to abstract
				addInfo(recordWrapper);
			}

			final Variant variant = new Variant(new Coordinate(activeWindowContig, referencePosition), 
					Character.toString(referenceBase), Character.toString(mutatedBase));
			variants[conditionIndex] = variant;
		}
		
		return variants;
	}

	private char mutateBase(char referenceBase) {
		int i = -1;
		while (i == -1 || BaseCallConfig.BASES[i] == referenceBase) {
			i = random.nextInt(BaseCallConfig.BASES.length);
		}
		
		return BaseCallConfig.BASES[i];
	}

	private ParallelData<T> updateParallelData(final int windowPosition) {
		final int conditions = parameters.getConditions().size();
		parallelData.reset();
		
		final int referencePosition = activeWindowStart + windowPosition;

		// update of coordinates not needed
		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			final Condition<T> condition = parameters.getConditions().get(conditionIndex);
			final List<SAMRecordWrapper> recordWrappers = caches[conditionIndex].getRecordWrapper(windowPosition);

			T data = parameters.getMethodFactory().createData();
			parallelData.setData(conditionIndex, data);

			for (final SAMRecordWrapper recordWrapper : recordWrappers) {
				// ignore invalid records
				if (! recordWrapper.isValid()) {
					continue;
				}

				// decode
				recordWrapper.processRecord();

				final byte baseQualtiy = recordWrapper.getBaseQuality(referencePosition);
				// ignore low quality base calls
				if (baseQualtiy < condition.getMinBASQ()) {
					continue;
				}

				final char baseCall = recordWrapper.getBaseCall(referencePosition);
				// ignore missing base calls
				if (baseCall == 'N') {
					continue;
				}

				final int baseIndex = BaseCallConfig.getInstance().getBaseIndex((byte)baseCall);
				parallelData.getData(conditionIndex).getBaseQualCount().add(baseIndex, baseQualtiy);
				parallelData.getData(conditionIndex).getRecordWrapper().add(recordWrapper);
			}
		}

		return parallelData;
	}

	private boolean mutate() {
		return random.nextDouble() <= parameters.getMutationRate();
	}
 
}
