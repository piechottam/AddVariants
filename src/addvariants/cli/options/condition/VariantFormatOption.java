package addvariants.cli.options.condition;

import jacusa.cli.options.AbstractACOption;

import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import addvariants.cli.parameters.AbstractParameters;
import addvariants.data.BaseQualRecordData;
import addvariants.io.variant.AbstractVariantFormat;
import addvariants.io.variant.BEDlikeVariantFormat;
import addvariants.io.variant.VCFVariantFormat;

public class VariantFormatOption<T extends BaseQualRecordData> 
extends AbstractACOption {

	private AbstractParameters<T> parameters;
	private Map<Character, AbstractVariantFormat<T>> formats;

	public VariantFormatOption(final AbstractParameters<T> parameters, final Map<Character, AbstractVariantFormat<T>> formats) {
		super("V", "variant-format");
		this.parameters = parameters;
		this.formats = formats;
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		StringBuffer sb = new StringBuffer();

		final AbstractVariantFormat<T> recordFormat = parameters.getVariantFormat();

		for (final char c : formats.keySet()) {
			AbstractVariantFormat<T> format = formats.get(c);
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
			.withDescription("Choose variant output format:\n" + sb.toString())
			.create(getOpt()); 
	}
	
	@Override
	public void process(CommandLine line) throws Exception {
		if(line.hasOption(getOpt())) {
			String s = line.getOptionValue(getOpt());
			if (s.length() != 1) {
				throw new IllegalArgumentException("Unknown variant format: " + s);
			}
			char c = s.charAt(0);
			if (! formats.containsKey(c)) {
				throw new IllegalArgumentException("Unknown variant format: " + c);
			}
			AbstractVariantFormat<T> recordFormat = null;
			switch (c) {
			case BEDlikeVariantFormat.CHAR:
					recordFormat = new BEDlikeVariantFormat<T>();
					break;
			case VCFVariantFormat.CHAR:
					recordFormat = new VCFVariantFormat<T>();
					break;
					default:
					throw new IllegalArgumentException("Unknown variant format: " + c);		
			}
			parameters.setVariantFormat(recordFormat);
		}
	}

}
