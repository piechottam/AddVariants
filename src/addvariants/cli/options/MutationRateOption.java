package addvariants.cli.options;

import lib.cli.options.AbstractACOption;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import addvariants.cli.parameters.RandomMutationsParameters;

public class MutationRateOption extends AbstractACOption {

	final private RandomMutationsParameters<?> parameters;
	
	public MutationRateOption(final RandomMutationsParameters<?> parameters) {
		super("M", "mutation-rate");
		this.parameters = parameters;
	}
	
	@Override
	public Option getOption() {
		return Option.builder(getOpt())
				.longOpt(getLongOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
		        .desc("Mutation rate " + getLongOpt().toUpperCase() + " \n default: " + parameters.getMutationRate())
		        .build();
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