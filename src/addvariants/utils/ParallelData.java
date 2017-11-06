package addvariants.utils;

import jacusa.data.hasCoordinate;
import jacusa.util.Coordinate;

import addvariants.data.BaseQualRecordData;
import addvariants.method.AbstractMethodFactory;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class ParallelData<T extends BaseQualRecordData> 
implements hasCoordinate {
	
	private AbstractMethodFactory<T> methodFactory;
	
	private Coordinate coordinate;

	private T[] data;
	private T cachedCombinedData;
	
	public ParallelData(final AbstractMethodFactory<T> methodFactory) {
		this.methodFactory 		= methodFactory;
		reset();
	}

	public ParallelData(final AbstractMethodFactory<T> methodFactory, 
			final Coordinate coordinate, final T[] data) {
		this.methodFactory 	= methodFactory;
		this.coordinate 	= new Coordinate(coordinate);
		
		this.data = data;
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param parallelData
	 */
	public ParallelData(final ParallelData<T> parallelData) {
		methodFactory = parallelData.methodFactory;
		coordinate = new Coordinate(parallelData.getCoordinate());

		// copy data
		data = methodFactory.copyContainerData(parallelData.data);
		cachedCombinedData = methodFactory.copyData(parallelData.cachedCombinedData);
	}
	
	public Coordinate getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(final Coordinate coordinate) {
		this.coordinate = coordinate;
	}
	
	public void setData(T[] data) {
		this.data = data;
		resetCache();
	}

	// make this faster remove data and add new
	public void setData(int conditionIndex, T data) {
		this.data[conditionIndex] = data;
		cachedCombinedData = null;
	}

	public void reset() {
		coordinate 	= new Coordinate();
		
		if (data != null) {
			data = methodFactory.createContainerData(data.length);
		} else {
			final int conditions = methodFactory.getParameters().getConditions().size();
			data = methodFactory.createContainerData(conditions);
		}

		resetCache();
	}
	
	protected void resetCache() {
		cachedCombinedData = null;
	}

	public T getData(int conditionIndex) {
		return data[conditionIndex];
	}
	
	public T getCombinedData() {
		if (cachedCombinedData == null) {
			cachedCombinedData = methodFactory.createData();

			for (int conditionIndex = 0; conditionIndex < getConditions(); conditionIndex++) {
				cachedCombinedData.add(getData(conditionIndex));
			}
		}
		
		return cachedCombinedData;
	}

	public int getConditions() {
		return data.length;
	}

	public ParallelData<T> copy() {
		return new ParallelData<T>(this);
	}

	public static <S extends BaseQualRecordData> void prettyPrint(final ParallelData<S> parallelPileupData) {
		final StringBuilder sb = new StringBuilder();

		// coordinate
		sb.append("Container Coordinate: ");
		sb.append(parallelPileupData.getCoordinate().toString());
		sb.append('\n');

		// pooled
		sb.append("Container combined pooled: \n");
		sb.append(parallelPileupData.getCombinedData().toString());
		sb.append('\n');

		System.out.print(sb.toString());
	}
	
}
