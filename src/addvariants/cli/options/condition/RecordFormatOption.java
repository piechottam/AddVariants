package addvariants.cli.options.condition;

import java.util.Map;

import lib.cli.options.AbstractACOption;
import lib.data.AbstractData;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasRecordWrapper;
import lib.io.record.AbstractRecordFormat;
import lib.io.record.BAMRecordFormat;
import lib.io.record.FASTQRecordFormat;
import lib.io.record.SAMRecordFormat;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import addvariants.cli.parameters.AddVariantsParameters;
import addvariants.cli.parameters.AddVariantsConditionParameter;

public class RecordFormatOption<T extends AbstractData & hasBaseCallCount & hasRecordWrapper> 
extends AbstractACOption {

	private AddVariantsParameters<T> parameter;
	private Map<Character, AbstractRecordFormat> formats;

	public RecordFormatOption(final AddVariantsParameters<T> parameters, final Map<Character, AbstractRecordFormat> formats) {
		super("R", "record-format");
		this.parameter = parameters;
		this.formats = formats;
	}

	@Override
	public Option getOption() {
		StringBuffer sb = new StringBuffer();

		final AddVariantsConditionParameter<T> condition = parameter.getConditionParameter(0); 
		final AbstractRecordFormat recordFormat = condition.getRecordFormat();

		for (final char c : formats.keySet()) {
			AbstractRecordFormat format = formats.get(c);
			if (format.getC() == recordFormat.getC()) {
				sb.append("<*>");
			} else {
				sb.append("< >");
			}
			sb.append(" " + c);
			sb.append(": ");
			sb.append(format.getDesc());
			sb.append("\n");
		}
		
		return Option.builder(getOpt())
				.longOpt(getLongOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.desc("Choose record tput format:\n" + sb.toString())
				.build(); 
	}

	@Override
	public void process(final CommandLine line) throws Exception {
		if (line.hasOption(getOpt())) {
			String s = line.getOptionValue(getOpt());
			if (s.length() != 1) {
				throw new IllegalArgumentException("Unknown record format: " + s);
			}
			char c = s.charAt(0);
			if (! formats.containsKey(c)) {
				throw new IllegalArgumentException("Unknown record format: " + c);
			}
			for (int conditionIndex = 0; conditionIndex < parameter.getConditionsSize(); conditionIndex++) {
				final AddVariantsConditionParameter<T> conditionParameter = parameter.getConditionParameter(conditionIndex);

				AbstractRecordFormat recordFormat = null;
				switch (c) {
				case SAMRecordFormat.CHAR:
						recordFormat = new SAMRecordFormat();
				case BAMRecordFormat.CHAR:
						recordFormat = new BAMRecordFormat();
						break;
				case FASTQRecordFormat.CHAR:
					recordFormat = new FASTQRecordFormat();
					break;
				
					default:
						throw new IllegalArgumentException("Unknown record format: " + c);		
				}
				conditionParameter.setRecordFormat(recordFormat);
			}
		}
	}

}