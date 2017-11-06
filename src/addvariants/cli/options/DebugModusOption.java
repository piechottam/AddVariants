package addvariants.cli.options;

import jacusa.cli.options.AbstractACOption;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import addvariants.cli.parameters.AbstractParameters;

public class DebugModusOption extends AbstractACOption {

	final private AbstractParameters<?> parameters;
	
	public DebugModusOption(final AbstractParameters<?> parameters) {
		super("x", "debug");
		this.parameters = parameters;
	}
	
	@Override
	public void process(CommandLine line) throws Exception {
		if(line.hasOption(getOpt())) {
			parameters.setDebug(true);
			parameters.getMethodFactory().debug();
	    }
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(getLongOpt())
		        .withDescription("turn on Debug modus")
		        .create(getOpt());
	}

}