package addvariants.cli.options.condition;

import jacusa.cli.options.AbstractACOption;

import java.io.File;
import java.nio.file.FileAlreadyExistsException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import addvariants.cli.parameters.AbstractParameters;

public class VariantFilenameOption 
extends AbstractACOption {

	private AbstractParameters<?> parameters;
	
	public VariantFilenameOption(AbstractParameters<?> parameters) {
		super("v", "variant-output");
		this.parameters = parameters;
	}
	
	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		String s = "Filename to store implanted variants\ndefault: " + parameters.getVariantFilename();

		return OptionBuilder.withLongOpt(getLongOpt())
			.withArgName(getLongOpt().toUpperCase())
			.hasArg(true)
	        .withDescription(s)
	        .create(getOpt());
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if(line.hasOption(getOpt())) {
			final String variantFilename = line.getOptionValue(getOpt());
			final File file = new File(variantFilename);
			if (file.exists()) {
				throw new FileAlreadyExistsException(variantFilename);
			}
			parameters.setVariantFilename(variantFilename);
		}
	}

}
