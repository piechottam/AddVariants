package addvariants.method;

import lib.cli.parameters.AbstractParameters;
import lib.tmp.SAMRecordModifier;

import addvariants.data.BaseQualRecordData;

/**
 * 
 * @author Michael Piechotta
 *
 */
public abstract class AbstractMethodFactory<T extends BaseQualRecordData> 
extends lib.method.AbstractMethodFactory<T> {
	
	public AbstractMethodFactory(final String name, final String desc, final AbstractParameters<T> parameters) {
		super(name, desc, parameters);
	}
	
	protected String getFiles() {
		String files = "[OPTIONS]";
		if (getParameters().getConditionsSize() == 1) {
			files += " BAM";
		} else {
			for (int conditionIndex = 0; conditionIndex < getParameters().getConditionsSize(); conditionIndex++) {
				files += " BAM" + (conditionIndex + 1);
			}
		}
		return files;
	}

	public abstract SAMRecordModifier createRecordModifier();

}
