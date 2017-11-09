package addvariants.cli.options.condition;

import java.io.File;
import java.nio.file.FileAlreadyExistsException;

import lib.cli.options.AbstractACOption;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import addvariants.cli.parameters.AddVariantsParameters;

public class VariantFilenameOption 
extends AbstractACOption {

	private AddVariantsParameters<?> parameters;
	
	public VariantFilenameOption(AddVariantsParameters<?> parameters) {
		super("v", "variant-output");
		this.parameters = parameters;
	}
	
	@Override
	public Option getOption() {
		String s = "Filename to store implanted variants\ndefault: " + parameters.getVariantFilename();

		return Option.builder(getOpt())
				.longOpt(getLongOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.desc(s)
				.build();
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
