package addvariants.cli.options.condition.filter;

import java.util.List;

import addvarians.cli.options.condition.filter.samtag.SamTagFilter;
import addvarians.cli.options.condition.filter.samtag.SamTagNHFilter;
import addvariants.cli.parameters.Condition;
import addvariants.data.BaseQualRecordData;

public class FilterNHsamTagOption<T extends BaseQualRecordData> extends FilterSamTagConditionOption<T> {

	private static final String TAG = "NH";
	
	public FilterNHsamTagOption(final int conditionIndex, final Condition<T> condition) {
		super(conditionIndex, condition, TAG);
	}

	public FilterNHsamTagOption(final List<Condition<T>> conditions) {
		super(conditions, TAG);
	}
	
	@Override
	protected SamTagFilter createSamTagFilter(int value) {
		return new SamTagNHFilter(value);
	}

}