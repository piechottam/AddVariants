package addvariants.cli.options.condition;

import jacusa.cli.options.AbstractACOption;

import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import addvariants.cli.parameters.AbstractParameters;
import addvariants.cli.parameters.Condition;
import addvariants.data.BaseQualRecordData;
import addvariants.io.record.AbstractRecordFormat;
import addvariants.io.record.BAMRecordFormat;
import addvariants.io.record.FASTQRecordFormat;
import addvariants.io.record.SAMRecordFormat;

public class RecordFormatOption<T extends BaseQualRecordData> 
extends AbstractACOption {

	private AbstractParameters<T> parameters;
	private Map<Character, AbstractRecordFormat<T>> formats;

	public RecordFormatOption(final AbstractParameters<T> parameters, final Map<Character, AbstractRecordFormat<T>> formats) {
		super("R", "record-format");
		this.parameters = parameters;
		this.formats = formats;
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		StringBuffer sb = new StringBuffer();

		final Condition<T> condition = parameters.getConditions().get(0); 
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
		
		return OptionBuilder.withLongOpt(getLongOpt())
			.withArgName(getLongOpt().toUpperCase())
			.hasArg(true)
			.withDescription("Choose record tput format:\n" + sb.toString())
			.create(getOpt()); 
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
			for (final Condition<T> condition : parameters.getConditions()) {
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