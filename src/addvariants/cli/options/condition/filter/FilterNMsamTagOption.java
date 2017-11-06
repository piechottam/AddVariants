package addvariants.cli.options.condition.filter;

import java.util.List;

import addvarians.cli.options.condition.filter.samtag.SamTagFilter;
import addvarians.cli.options.condition.filter.samtag.SamTagNMFilter;
import addvariants.cli.parameters.Condition;
import addvariants.data.BaseQualRecordData;

public class FilterNMsamTagOption<T extends BaseQualRecordData> extends FilterSamTagConditionOption<T> {
	
	private static final String TAG = "NM";
	
	public FilterNMsamTagOption(final int conditionIndex, final Condition<T> condition) {
		super(conditionIndex, condition, TAG);
	}

	public FilterNMsamTagOption(final List<Condition<T>> conditions) {
		super(conditions, TAG);
	}
	
	@Override
	protected SamTagFilter createSamTagFilter(int value) {
		return new SamTagNMFilter(value);
	}

}
