package addvariants.io;

import java.util.List;

import addvariants.cli.parameters.Condition;
import addvariants.data.BaseQualRecordData;

/**
 * 
 * @author Michael Piechotta
 *
 */
public abstract class AbstractWriter<T extends BaseQualRecordData> 
implements Cloneable {

	private String filename;
	private AbstractFormat<T> format;

	public AbstractWriter(final String filename, AbstractFormat<T> format) {
		this.filename = filename;
		this.format = format;
	}

	public String getFilename() {
		return filename;
	}

	public AbstractFormat<T> getFormat() {
		return format;
	}

	public abstract void addHeader(List<Condition<T>> conditions); 
	public abstract void close();
	
}
