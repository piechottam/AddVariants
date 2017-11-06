package addvariants.method;

import jacusa.cli.options.AbstractACOption;
import jacusa.util.Coordinate;
import jacusa.util.coordinateprovider.CoordinateProvider;
import jacusa.util.coordinateprovider.SAMCoordinateProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import addvariants.AddVariants;
import addvariants.cli.options.SAMPathnameArg;
import addvariants.cli.parameters.AbstractParameters;
import addvariants.data.BaseQualRecordData;
import addvariants.dispatcher.WorkerDispatcher;
import addvariants.worker.SAMRecordModifier;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMSequenceRecord;

/**
 * 
 * @author Michael Piechotta
 *
 */
public abstract class AbstractMethodFactory<T extends BaseQualRecordData> {

	private final String name;
	private final String desc;

	private AbstractParameters<T> parameters;

	private final Set<AbstractACOption> ACOptions;

	private CoordinateProvider coordinateProvider;
	
	public AbstractMethodFactory(final String name, final String desc, 
			final AbstractParameters<T> parameters) {
		this.name = name;
		this.desc = desc;

		setParameters(parameters);
		ACOptions = new HashSet<AbstractACOption>(10);
	}
	
	// needed for Methods where the number of conditions is unknown... 
	public void initParameters(final int conditions) { }
	
	protected void setParameters(final AbstractParameters<T> parameters) {
		parameters.setMethodFactory(this);
		this.parameters = parameters;
		
	}
	
	public AbstractParameters<T> getParameters() {
		return parameters;
	}

	public abstract void initACOptions();

	protected void addACOption(AbstractACOption newACOption) {
		if (checkDuplicate(newACOption)) {
			ACOptions.add(newACOption);
		}
	}
	
	private boolean checkDuplicate(final AbstractACOption newACOption) {
		for (final AbstractACOption ACOption : ACOptions) {
			try {
				if (! ACOption.getOpt().isEmpty() && 
						ACOption.getOpt().equals(newACOption.getOpt())) {
					throw new Exception("Duplicate opt '" + newACOption.getOpt() + 
							"' for object: " + newACOption.toString());
				}
				if (! ACOption.getOpt().isEmpty() && 
						ACOption.getLongOpt().equals(newACOption.getLongOpt())) {
					throw new Exception("Duplicate longOpt '" + newACOption.getLongOpt() + 
							"' for object" + newACOption.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

		return true;
	}

	/**
	 * 
	 * @param pathnames
	 * @param coordinateProvider
	 * @return
	 * @throws IOException
	 */
	public WorkerDispatcher<T> createWorkerDispatcher(
			final CoordinateProvider coordinateProvider) throws IOException {
		return new WorkerDispatcher<T>(this, coordinateProvider);
	}

	/**
	 * 
	 * @return
	 */
	public Set<AbstractACOption> getACOptions() {
		return ACOptions;
	}

	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return
	 */
	public String getDescription() {
		return desc;
	}
	
	/**
	 * 
	 * @param options
	 */
	public void printUsage() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.setWidth(160);

		Set<AbstractACOption> acOptions = getACOptions();
		Options options = new Options();
		for (AbstractACOption acoption : acOptions) {
			options.addOption(acoption.getOption());
		}
		
		String files = "[OPTIONS]";
		if (getParameters().getConditions().size() == 1) {
			files += " BAM";
		} else {
			for (int conditionIndex = 0; conditionIndex < getParameters().getConditions().size(); conditionIndex++) {
				files += " BAM" + (conditionIndex + 1);
			}
		}
		
		formatter.printHelp(
				AddVariants.JAR + 
				" " + 
				getName() +
				" " +
				files, 
				options);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void initCoordinateProvider() throws Exception {
		final int conditions = parameters.getConditions().size();
		final String[] pathnames = new String[conditions];
		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			pathnames[conditionIndex] = parameters
					.getConditions().get(conditionIndex)
					.getInputFilename();
		}

		final List<SAMSequenceRecord> records = getSAMSequenceRecords(pathnames);
		coordinateProvider = new SAMCoordinateProvider(records);
	}

	/**
	 * 
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public boolean parseArgs(String[] args) throws Exception {
		for (int conditionIndex = 0; conditionIndex < args.length; conditionIndex++) {
			SAMPathnameArg pa = new SAMPathnameArg(conditionIndex + 1, parameters.getConditions().get(conditionIndex));
			pa.processArg(args[conditionIndex]);
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param pathnames
	 * @return
	 * @throws Exception
	 */
	protected List<SAMSequenceRecord> getSAMSequenceRecords(String[] pathnames) throws Exception {
		AddVariants.printLog("Computing overlap between sequence records.");

		SAMFileReader reader 			= new SAMFileReader(new File(pathnames[0]));
		List<SAMSequenceRecord> records = reader.getFileHeader().getSequenceDictionary().getSequences();
		// close readers
		reader.close();

		return records;
	}
	
	/**
	 * 
	 * @return
	 */
	public CoordinateProvider getCoordinateProvider() {
		return coordinateProvider;
	}
	
	/**
	 * 
	 * @param pathnames
	 * @return
	 * @throws Exception
	 */
	protected List<SAMSequenceRecord> getSAMSequenceRecords(String[][] pathnames) throws Exception {
		String error = "Sequence Dictionaries of BAM files do not match";

		List<SAMSequenceRecord> records = getSAMSequenceRecords(pathnames[0]);

		List<Coordinate> coordinates = new ArrayList<Coordinate>();
		Set<String> targetSequenceNames = new HashSet<String>();
		for(SAMSequenceRecord record : records) {
			coordinates.add(new Coordinate(record.getSequenceName(), 1, record.getSequenceLength()));
			targetSequenceNames.add(record.getSequenceName());
		}

		for (int conditionIndex = 0; conditionIndex < pathnames.length; conditionIndex++) {
			if (! isValid(targetSequenceNames, pathnames[conditionIndex])) {
				throw new Exception(error);
			}
		}

		return records;		
	}

	/**
	 * 
	 * @param targetSequenceNames
	 * @param pathnames
	 * @return
	 */
	private boolean isValid(Set<String> targetSequenceNames, String[] pathnames) {
		Set<String> sequenceNames = new HashSet<String>();
		for(String pathname : pathnames) {
			SAMFileReader reader = new SAMFileReader(new File(pathname));
			List<SAMSequenceRecord> records	= reader.getFileHeader().getSequenceDictionary().getSequences();
			for(SAMSequenceRecord record : records) {
				sequenceNames.add(record.getSequenceName());
			}	
			reader.close();
		}

		if(!sequenceNames.containsAll(targetSequenceNames) || !targetSequenceNames.containsAll(sequenceNames)) {
			return false;
		}

		return true;
	}

	public void debug() {};

	public abstract SAMRecordModifier createRecordModifier();

	public abstract T createData();
	public abstract T[] createContainerData(final int n);

	public abstract T copyData(final T data);
	public abstract T[] copyContainerData(final T[] data);

}
