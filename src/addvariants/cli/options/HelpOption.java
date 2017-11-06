package addvariants.cli.options;

import jacusa.cli.options.AbstractACOption;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import addvariants.cli.CLI;

public class HelpOption extends AbstractACOption {

	private CLI cmd;
	
	public HelpOption(final CLI cmd) {
		super("h", "help");

		this.cmd = cmd;
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(getLongOpt())
				.hasArg(false)
				.withDescription("Print usage information")
				.create(getOpt());
	}

	@Override
	public void process(CommandLine line) {
		if (line.hasOption(getOpt())) {
	    	cmd.printUsage(); 
	    	System.exit(0);
	    }
	}

}