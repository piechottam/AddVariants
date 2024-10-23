package addvariants.cli.parameters;

import lib.cli.parameter.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasRecordWrapper;

public class AddVariantsConditionParameter<T extends AbstractData & hasBaseCallCount & hasRecordWrapper> 
extends AbstractConditionParameter<T> {
	
	private boolean adjustSAMTag;
	
	public AddVariantsConditionParameter(final int conditionIndex) {
		super(conditionIndex);

		adjustSAMTag = true;
	}

	public boolean adjustSAMTag() {
		return adjustSAMTag;
	}
	
	public void setAdjustSAMTag(final boolean adjustSAMTag) {
		this.adjustSAMTag = adjustSAMTag;
	}

}
