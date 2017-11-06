package addvariants.cli.options;

import java.io.File;
import java.io.FileNotFoundException;

import addvariants.cli.parameters.Condition;

import net.sf.samtools.SAMFileReader;

public class SAMPathnameArg {

	public static final char SEP = ',';

	private int conditionIndex;
	private Condition<?> condition;
	
	public SAMPathnameArg(final int conditionIndex, Condition<?> paramteres) {
		this.conditionIndex = conditionIndex;
		this.condition = paramteres;
	}

	public void processArg(String arg) throws Exception {
		final String pathname = arg;
    	final File file = new File(pathname);
    	if (! file.exists()) {
    		throw new FileNotFoundException("File (" + pathname + ") in not accessible!");
    	}
    	final SAMFileReader reader = new SAMFileReader(file);
    	if (! reader.hasIndex()) {
    		reader.close();
    		throw new FileNotFoundException("Index for BAM file" + conditionIndex + " is not accessible!");
    	}
    	reader.close();
		condition.setInputFilename(pathname);
	}

}
