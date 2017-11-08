package addvariants.cli.options.condition;


import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;

import lib.cli.options.condition.AbstractConditionACOption;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import com.sun.org.apache.xalan.internal.xsltc.cmdline.getopt.GetOpt;

import addvariants.cli.parameters.ConditionParameter;
import addvariants.data.BaseQualRecordData;

public class RecordFilenameOption<T extends BaseQualRecordData> 
extends AbstractConditionACOption<T> {

	// TODO multiple files
	
	private final static String OPT = "r";
	private final static String LONG_OPT = "record-output";
	
	public final static String SUFFIX = "modified";
	
	public RecordFilenameOption(final int conditionIndex, final ConditionParameter<T> conditionParameter) {
		super(OPT, LONG_OPT, conditionIndex, conditionParameter);
	}

	public RecordFilenameOption(final List<ConditionParameter<T>> conditionParameters) {
		super(OPT, LONG_OPT, conditionParameters);
	}

	@Override
	public Option getOption() {
		final StringBuilder sb = new StringBuilder();

		//if (getConditionIndex() >= 0) {
			sb.append("Write records/reads for condition " + getConditionIndex() + " to " + getLongOpt());
			sb.append("\nDefault: BAM" + getConditionIndex() + "_" + SUFFIX);
		//} else if (getConditions().size() > 1) {
		//	sb.append("Write all records/reads to directory: " + getLongOpt());
		//	sb.append("\nDefault: (Current directory)");
		//}

		return Option.builder(getOpt())
				.longOpt(getLongOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.desc(sb.toString())
				.build();
	}

	@Override
	public void process(final CommandLine line) throws Exception {
		if(line.hasOption(getOpt())) {
			final String recordFilename = line.getOptionValue(getOpt());
			// check if file/dir already exists
		 	final File file = new File(recordFilename);
		 	if (file.exists()) {
		 		throw new FileAlreadyExistsException(recordFilename);
		 	}
		 	// set record output filename
			for (final ConditionParameter<T> condition : getConditionParameters()) {
	    		condition.setRecordFilename(recordFilename);
	    	}
			
			
			/* TODO
			if (getConditionIndex() >= 0) { // file
				
			} else { // directory
				final String recordOutputDir = line.getOptionValue(OPT);
				// check if file/dir already exists
				final File file = new File(recordOutputDir);
				if (file.exists()) {
			 		throw new FileAlreadyExistsException(recordOutputDir);
			 	}
				// set output directory for records  
				for (final Condition<T> condition : getConditions()) {
					final String recordFilename = recordOutputDir + "/" + condition.getInputFilename();
		    		condition.setRecordFilename(recordFilename);
		    	}
			}
			*/
		}
	}

}
