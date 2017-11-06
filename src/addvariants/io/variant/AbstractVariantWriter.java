package addvariants.io.variant;

import java.util.List;

import addvariants.cli.parameters.Condition;
import addvariants.data.BaseQualRecordData;
import addvariants.io.AbstractWriter;
import addvariants.utils.Variant;

/**
 * 
 * @author Michael Piechotta
 *
 */
public abstract class AbstractVariantWriter<T extends BaseQualRecordData> 
extends AbstractWriter<T> {

	public AbstractVariantWriter(final String filename, final AbstractVariantFormat<T> format) {
		super(filename, format);
	}

	public abstract void addVariants(final Variant[] variants, List<Condition<T>> conditions) throws Exception;

}
