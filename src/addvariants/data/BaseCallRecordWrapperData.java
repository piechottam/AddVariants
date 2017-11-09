package addvariants.data;

import java.util.ArrayList;
import java.util.List;

import lib.data.AbstractData;
import lib.data.basecall.BaseCallCount;
import lib.data.builder.SAMRecordWrapper;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasRecordWrapper;
import lib.util.Coordinate;

public class BaseCallRecordWrapperData
extends AbstractData
implements hasBaseCallCount, hasRecordWrapper {

	private BaseCallCount baseCallCount;
	private List<SAMRecordWrapper> recordWrapper;
	
	public BaseCallRecordWrapperData() {
		super();

		baseCallCount = new BaseCallCount();
		recordWrapper = new ArrayList<SAMRecordWrapper>(50);
	}

	public BaseCallRecordWrapperData(final BaseCallRecordWrapperData pileupData) {
		super(pileupData);
		this.baseCallCount = pileupData.baseCallCount .copy();
	}
	
	public BaseCallRecordWrapperData(final Coordinate coordinate) {
		super(coordinate);

		baseCallCount = new BaseCallCount();
	}
		
	@Override
	public BaseCallCount getBaseCallCount() {
		return baseCallCount;
	}

	public void add(AbstractData abstractData) {
		BaseCallRecordWrapperData data = (BaseCallRecordWrapperData) abstractData;
		this.baseCallCount.add(data.getBaseCallCount());
		this.recordWrapper.addAll(data.getRecordWrapper());
	}

	@Override
	public List<SAMRecordWrapper> getRecordWrapper() {
		return recordWrapper;
	}
	
	@Override
	public BaseCallRecordWrapperData copy() {
		return new BaseCallRecordWrapperData(this);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Base count: ");
		sb.append(getBaseCallCount().toString());
		sb.append('\n');
		sb.append("Wrapped SAMRecord(s): ");
		sb.append(recordWrapper.size());
		return sb.toString();
	}
	
}
