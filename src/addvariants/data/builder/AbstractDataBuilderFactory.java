package addvariants.data.builder;

import addvariants.cli.parameters.Condition;
import jacusa.util.Coordinate;
import net.sf.samtools.SAMFileReader;

/**
 * 
 * @author Michael Piechotta
 *
 */
public abstract class AbstractDataBuilderFactory {
	
	public AbstractDataBuilderFactory() {}
	
	/**
	 * 
	 * @param windowCoordinates
	 * @param reader
	 * @param condition
	 * @param parameters
	 * @return
	 */
	public abstract SAMRecordWrapperProvider newInstance(
			final Coordinate windowCoordinates, 
			final SAMFileReader reader, 
			final Condition<?> condition);

}
