package addvariants.worker;

import java.util.Iterator;
import java.util.List;

import addvariants.data.builder.SAMRecordWrapper;

public class CombinedSAMRecordWrapperIterator implements Iterator<SAMRecordWrapper> {
	
	private Iterator<SAMRecordWrapper> current;
	private final Iterator<Iterator<SAMRecordWrapper>> iterator;

	public CombinedSAMRecordWrapperIterator(final List<Iterator<SAMRecordWrapper>> iterators) {
		iterator = iterators.iterator();
		if (iterator.hasNext()) {
			current = iterator.next();
		}
	}
	
	@Override
	public boolean hasNext() {
		if (current.hasNext()) {
			return true;
		}
		
		if (iterator.hasNext()){
			current = iterator.next();
			return true;
		}

		return false;
	}
	
	@Override
	public SAMRecordWrapper next() {
		if (! hasNext()) {
			return null;
		}

		return current.next();
	}

}
