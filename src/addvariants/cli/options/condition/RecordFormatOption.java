package addvariants.cli.options.condition;

import java.util.Map;

import lib.cli.options.AbstractACOption;
import lib.io.record.AbstractRecordFormat;
import lib.io.record.BAMRecordFormat;
import lib.io.record.FASTQRecordFormat;
import lib.io.record.SAMRecordFormat;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import addvariants.cli.parameters.AbstractParameters;
import addvariants.cli.parameters.ConditionParameter;
import addvariants.data.BaseQualRecordData;

public class RecordFormatOption<T extends BaseQualRecordData> 
extends AbstractACOption {

	private AbstractParameters<T> parameters;
	private Map<Character, AbstractRecordFormat<T>> formats;

	public RecordFormatOption(final AbstractParameters<T> parameters, final Map<Character, AbstractRecordFormat<T>> formats) {
		super("R", "record-format");
		this.parameters = parameters;
		this.formats = formats;
	}

	@Override
	public Option getOption() {
		StringBuffer sb = new StringBuffer();

		final ConditionParameter<T> condition = parameters.getConditionsSize().get(0); 
		final AbstractRecordFormat<T> recordFormat = condition.getRecordFormat();

		for (final char c : formats.keySet()) {
			AbstractRecordFormat<T> format = formats.get(c);
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
			for (final ConditionParameter<T> condition : parameters.getConditionsSize()) {
				AbstractRecordFormat<T> recordFormat = null;
				switch (c) {
				case SAMRecordFormat.CHAR:
						recordFormat = new SAMRecordFormat<T>();
				case BAMRecordFormat.CHAR:
						recordFormat = new BAMRecordFormat<T>();
						break;
				case FASTQRecordFormat.CHAR:
					recordFormat = new FASTQRecordFormat<T>();
					break;
				
					default:
						throw new IllegalArgumentException("Unknown record format: " + c);		
				}
				condition.setRecordFormat(recordFormat);
			}
		}
	}

}