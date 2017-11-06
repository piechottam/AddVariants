package addvariants.data;

import java.util.ArrayList;
import java.util.List;

import addvariants.data.builder.SAMRecordWrapper;
import addvariants.data.builder.hasRecords;

import jacusa.data.AbstractData;
import jacusa.data.BaseQualData;
import jacusa.data.hasBaseQualCount;
import jacusa.pileup.builder.hasLibraryType;

public class BaseQualRecordData
extends BaseQualData
implements hasBaseQualCount, hasRecords, hasLibraryType {

	private List<SAMRecordWrapper> records;
	
	public BaseQualRecordData() {
		super();
		
		records = new ArrayList<SAMRecordWrapper>(100);
	}

	public BaseQualRecordData(final BaseQualRecordData pileupData) {
		super(pileupData);
		
	}
	public void add(AbstractData abstractData) {
		super.add(abstractData);
		
		BaseQualRecordData e = (BaseQualRecordData) abstractData;
		records.addAll(e.getRecordWrapper());
	}

	public List<SAMRecordWrapper> getRecordWrapper() {
		return records;
	}
	
	@Override
	public BaseQualRecordData copy() {
		return new BaseQualRecordData(this);
	}

}
