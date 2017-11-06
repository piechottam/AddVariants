package addvariants.data;

import java.util.ArrayList;
import java.util.List;

import addvariants.data.builder.SAMRecordWrapper;

import jacusa.data.BaseQualCount;

public class BaseQualRecordCount extends BaseQualCount {

	// container
	private List<SAMRecordWrapper> records;

	public BaseQualRecordCount() {
		super();
		records = new ArrayList<SAMRecordWrapper>(100);
	}

	public BaseQualRecordCount(final int[] baseCount, final int[][] base2qual, final int[] minMapq, final List<SAMRecordWrapper> records) {
		super(baseCount, base2qual, minMapq);
		this.records.addAll(records);
	}
	
	public BaseQualRecordCount(BaseQualRecordCount e) {
		super(e);
		this.records.addAll(records);
	}

	public BaseQualRecordCount copy() {
		return new BaseQualRecordCount(this);
	}

	public List<SAMRecordWrapper> getRecords() {
		return records;
	}

	public String toString() {
		final StringBuilder sb = new StringBuilder();

		sb.append("Reads: ");
		sb.append(records.size());
		
		sb.append(" ");
		
		sb.append(super.toString());

		return sb.toString();
	}
	
}
