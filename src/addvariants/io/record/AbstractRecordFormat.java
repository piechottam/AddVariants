package addvariants.io.record;

import addvariants.cli.parameters.Condition;
import addvariants.data.BaseQualRecordData;
import addvariants.io.AbstractFormat;

/**
 * 
 * @author Michael Piechotta
 *
 */
public abstract class AbstractRecordFormat<T extends BaseQualRecordData> 
extends AbstractFormat<T> {

	public AbstractRecordFormat(char c, String desc) {
		super(c, desc);
	}

	public abstract AbstractRecordWriter<T> createWriterInstance(final Condition<T> condition);

}
