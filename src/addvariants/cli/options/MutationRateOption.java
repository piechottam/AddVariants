package addvariants.cli.options;

import jacusa.cli.options.AbstractACOption;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import addvariants.cli.parameters.RandomMutationsParameters;

public class MutationRateOption extends AbstractACOption {

	final private RandomMutationsParameters<?> parameters;
	
	public MutationRateOption(final RandomMutationsParameters<?> parameters) {
		super("M", "mutation-rate");
		this.parameters = parameters;
	}
	
	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(getLongOpt())
				.withArgName(getLongOpt().toUpperCase())
				.hasArg(true)
		        .withDescription("Mutation rate " + getLongOpt().toUpperCase() + " \n default: " + parameters.getMutationRate())
		        .create(getOpt());
	}

	@Override
	public void process(final CommandLine line) throws Exception {
		if (line.hasOption(getOpt())) {
	    	final double mutationRate = Double.parseDouble(line.getOptionValue(getOpt()));
	    	if(mutationRate >= 1.0 || mutationRate <= 0.0) {
	    		throw new IllegalArgumentException(getLongOpt().toUpperCase() + " must be > 0.0 and < 1.0!");
	    	}
	    	parameters.setMutationRate(mutationRate);
	    }
	}

}