package addvariants.worker;

import jacusa.util.Coordinate;

import java.util.Iterator;
import java.util.List;

import addvariants.data.builder.SAMRecordWrapper;
import addvariants.utils.Variant;

public interface SAMRecordModifier extends Iterator<Variant[]> {

	List<List<SAMRecordWrapper>> build(final Coordinate activeWindowCoordinates, 
			final List<Iterator<SAMRecordWrapper>> iterators);

	void addInfo(final SAMRecordWrapper record);
	String getParameterInfo();
	
}
