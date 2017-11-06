package addvariants.cli.options.condition;

import java.util.ArrayList;
import java.util.List;

import addvariants.cli.parameters.Condition;
import addvariants.data.BaseQualRecordData;

import jacusa.cli.options.AbstractACOption;

public abstract class AbstractConditionACOption<T extends BaseQualRecordData> extends AbstractACOption {

	private int conditionIndex;
	private List<Condition<T>> conditions;
		
	public AbstractConditionACOption(final String opt, final String longOpt, List<Condition<T>> conditions) {
		super(opt, longOpt);
		conditionIndex 	= -1;
		this.conditions = conditions;
	}
	
	public AbstractConditionACOption(final String opt, final String longOpt, final int conditionIndex, final Condition<T> condition) {
		super(! opt.isEmpty() ? opt + conditionIndex: new String(),
				! longOpt.isEmpty() ? longOpt + conditionIndex : new String());

		this.conditionIndex = conditionIndex;
		conditions = new ArrayList<Condition<T>>(1);
		conditions.add(condition);
	}
	
	public List<Condition<T>> getConditions() {
		return conditions;
	}
	
	public int getConditionIndex() {
		return conditionIndex;
	}

}
