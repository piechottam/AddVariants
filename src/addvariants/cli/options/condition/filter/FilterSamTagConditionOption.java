package addvariants.cli.options.condition.filter;

import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import addvarians.cli.options.condition.filter.samtag.SamTagFilter;
import addvariants.cli.options.condition.AbstractConditionACOption;
import addvariants.cli.parameters.Condition;
import addvariants.data.BaseQualRecordData;

public abstract class FilterSamTagConditionOption<T extends BaseQualRecordData> 
extends AbstractConditionACOption<T> {

	private static final String LONG_OPT = "filter";
	private String tag;

	public FilterSamTagConditionOption(
			final int conditionIndex, 
			final Condition<T> condition, final String tag) {
		super(new String(), LONG_OPT + tag, conditionIndex, condition);
		this.tag = tag;
	}

	public FilterSamTagConditionOption(final List<Condition<T>> conditions, final String tag) {
		super(new String(), LONG_OPT + tag, conditions);
		this.tag = tag;
	}
	
	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(getLongOpt())) {
	    	int value = Integer.parseInt(line.getOptionValue(getLongOpt()));
	    	for (final Condition<T> condition : getConditions()) {
	    		condition.getSamTagFilters().add(createSamTagFilter(value));
	    	}
	    }
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		String s = new String();

		if (getConditionIndex() >= 0) {
			s = " for condition " + getConditionIndex();
		} else if (getConditions().size() > 1) {
			s = " for all conditions";
		}
		s = "Max " + tag + "-VALUE for SAM tag " + s;

		return OptionBuilder.withLongOpt(getLongOpt())
				.withArgName(tag + "-VALUE")
				.hasArg(true)
		        .withDescription(s)
		        .create();
	}

	protected abstract SamTagFilter createSamTagFilter(int value);  

}
