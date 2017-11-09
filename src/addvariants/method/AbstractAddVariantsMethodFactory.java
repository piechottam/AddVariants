package addvariants.method;

import addvariants.cli.parameters.AddVariantsParameters;
import lib.cli.parameters.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasRecordWrapper;
import lib.method.AbstractMethodFactory;
import lib.tmp.SAMRecordModifier;

/**
 * 
 * @author Michael Piechotta
 *
 */
public abstract class AbstractAddVariantsMethodFactory<T extends AbstractData & hasBaseCallCount & hasRecordWrapper> 
extends AbstractMethodFactory<T> {
	
	public AbstractAddVariantsMethodFactory(final String name, final String desc, final AbstractParameter<T> parameters) {
		super(name, desc, parameters);
	}
	
	@Override
	protected String getFiles() {
		String files = "[OPTIONS]";
		if (getParameter().getConditionsSize() == 1) {
			files += " BAM";
		} else {
			for (int conditionIndex = 0; conditionIndex < getParameter().getConditionsSize(); conditionIndex++) {
				files += " BAM" + (conditionIndex + 1);
			}
		}
		return files;
	}

	@Override
	public AddVariantsParameters<T> getParameter() {
		return (AddVariantsParameters<T>) super.getParameter();
	}
	
	@Deprecated
	public abstract SAMRecordModifier createRecordModifier();

}
