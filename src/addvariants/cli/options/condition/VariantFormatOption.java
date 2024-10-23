 package addvariants.cli.options.condition;

import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import lib.cli.options.AbstractACOption;
import lib.data.AbstractData;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasRecordWrapper;

import addvariants.cli.parameters.AddVariantsParameters;
import addvariants.io.variant.AbstractVariantFormat;
import addvariants.io.variant.BEDlikeVariantFormat;
import addvariants.io.variant.VCFVariantFormat;

public class VariantFormatOption<T extends AbstractData & hasBaseCallCount & hasRecordWrapper> 
extends AbstractACOption {

	private AddVariantsParameters<T> parameters;
	private Map<Character, AbstractVariantFormat> formats;

	public VariantFormatOption(final AddVariantsParameters<T> parameters, final Map<Character, AbstractVariantFormat> formats) {
		super("V", "variant-format");
		this.parameters = parameters;
		this.formats = formats;
	}

	@Override
	public Option getOption() {
		StringBuffer sb = new StringBuffer();

		final AbstractVariantFormat recordFormat = parameters.getVariantFormat();

		for (final char c : formats.keySet()) {
			AbstractVariantFormat format = formats.get(c);
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
				.desc("Choose variant output format:\n" + sb.toString())
				.build();

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
			AbstractVariantFormat recordFormat = null;
			switch (c) {
			case BEDlikeVariantFormat.CHAR:
					recordFormat = new BEDlikeVariantFormat();
					break;
			case VCFVariantFormat.CHAR:
					recordFormat = new VCFVariantFormat();
					break;
					default:
					throw new IllegalArgumentException("Unknown variant format: " + c);		
			}
			parameters.setVariantFormat(recordFormat);
		}
	}

}
