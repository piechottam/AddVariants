package addvariants.cli.options;

import jacusa.cli.options.AbstractACOption;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import addvariants.cli.parameters.AbstractParameters;

public class ThreadWindowSizeOption extends AbstractACOption {

	final private AbstractParameters<?> parameters; 
	
	public ThreadWindowSizeOption(AbstractParameters<?> parameters) {
		super("W", "thread-window-size");
		this.parameters = parameters;
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(getLongOpt())
			.withArgName("THREAD-WINDOW-SIZE")
			.hasArg(true)
	        .withDescription("size of the window used per thread.\n default: " + parameters.getReservedWindowSize())
	        .create(getOpt());
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(getOpt())) {
	    	String value = line.getOptionValue(getOpt());
	    	int windowSize = Integer.parseInt(value);
	    	if (windowSize < 1) {
	    		throw new IllegalArgumentException("THREAD-WINDOW-SIZE too small: " + windowSize);
	    	}

	    	parameters.setReservedWindowSize(windowSize);
		}
	}

}
