package addvariants.cli.options.condition;

import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import addvariants.cli.parameters.Condition;
import addvariants.data.BaseQualRecordData;

public class AdjustSAMTagConditionOption<T extends BaseQualRecordData> extends AbstractConditionACOption<T> {

	private static final String OPT = "n";
	private static final String LONG_OPT = "not-adjust-tags";
	
	public AdjustSAMTagConditionOption(final int conditionIndex, final Condition<T> condition) {
		super(OPT, LONG_OPT, conditionIndex, condition);
	}
	
	public AdjustSAMTagConditionOption(final List<Condition<T>> conditions) {
		super(OPT, LONG_OPT, conditions);
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
		s = " do not adjust SAM tag(s) " + getLongOpt().toUpperCase();

		return OptionBuilder.withLongOpt(getLongOpt())
			.withArgName(getLongOpt().toUpperCase())
			.hasArg(false)
	        .withDescription(s)
	        .create(getOpt());
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if(line.hasOption(getOpt())) {
	    	getConditions().get(getConditionIndex()).setAdjustSAMTag(false); // TODO test
		}
	}

}
