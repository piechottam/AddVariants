package addvariants.method;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.ParseException;

import addvariants.cli.CLI;
import addvariants.cli.options.HelpOption;
import addvariants.cli.options.MaxThreadOption;
import addvariants.cli.options.MutationRateOption;
import addvariants.cli.options.ThreadWindowSizeOption;
import addvariants.cli.options.WindowSizeOption;
import addvariants.cli.options.condition.MinBASQConditionOption;
import addvariants.cli.options.condition.MinCoverageConditionOption;
import addvariants.cli.options.condition.MinMAPQConditionOption;
import addvariants.cli.options.condition.RecordFilenameOption;
import addvariants.cli.options.condition.RecordFormatOption;
import addvariants.cli.options.condition.VariantFilenameOption;
import addvariants.cli.options.condition.VariantFormatOption;
import addvariants.cli.options.condition.filter.FilterFlagConditionOption;
import addvariants.cli.options.condition.filter.FilterNHsamTagOption;
import addvariants.cli.options.condition.filter.FilterNMsamTagOption;
import addvariants.cli.parameters.Condition;
import addvariants.cli.parameters.RandomMutationsParameters;
import addvariants.data.BaseQualRecordData;
import addvariants.io.record.AbstractRecordFormat;
import addvariants.io.record.BAMRecordFormat;
import addvariants.io.record.FASTQRecordFormat;
import addvariants.io.record.SAMRecordFormat;
import addvariants.io.variant.AbstractVariantFormat;
import addvariants.io.variant.BEDlikeVariantFormat;
import addvariants.io.variant.VCFVariantFormat;
import addvariants.worker.RandomMutation;
import addvariants.worker.SAMRecordModifier;

public class RandomMutationsMethod1 extends AbstractMethodFactory<BaseQualRecordData> {

	public RandomMutationsMethod1() {
		super("mutate-1", "Add mutatations to 1 SAM/BAM file", new RandomMutationsParameters<BaseQualRecordData>(1));
	}
	
	@Override
	public RandomMutationsParameters<BaseQualRecordData> getParameters() {
		return (RandomMutationsParameters<BaseQualRecordData>) super.getParameters();
	}
	
	@Override
	public void initACOptions() {
		initGlobalACOptions();
		initConditionACOptions();
	}

	private void initGlobalACOptions() {
		addACOption(new MaxThreadOption(getParameters()));
		addACOption(new WindowSizeOption(getParameters()));
		addACOption(new ThreadWindowSizeOption(getParameters()));
		addACOption(new HelpOption(CLI.getSingleton()));

		addACOption(new MutationRateOption(getParameters()));
	}
	
	private void initConditionACOptions() {
		// result format
		if (getRecordFormat().size() == 1 ) {
			final Character[] a = getRecordFormat().keySet().toArray(new Character[1]);
			for (final Condition<BaseQualRecordData> condition : getParameters().getConditions()) {
				condition.setRecordFormat(getRecordFormat().get(a[0]));
			}
		} else {
			addACOption(new RecordFormatOption<BaseQualRecordData>(getParameters(), getRecordFormat()));
		}
		addACOption(new RecordFilenameOption<BaseQualRecordData>(getParameters().getConditions()));
		
		// variant format
		if (getVariantFormat().size() == 1 ) {
			final Character[] a = getVariantFormat().keySet().toArray(new Character[1]);
			getParameters().setVariantFormat(getVariantFormat().get(a[0]));
		} else {
			addACOption(new VariantFormatOption<BaseQualRecordData>(getParameters(), getVariantFormat()));
		}
		addACOption(new VariantFilenameOption(getParameters()));

		addACOption(new MinMAPQConditionOption<BaseQualRecordData>(getParameters().getConditions()));
		addACOption(new MinBASQConditionOption<BaseQualRecordData>(getParameters().getConditions()));
		addACOption(new MinCoverageConditionOption<BaseQualRecordData>(getParameters().getConditions()));
		addACOption(new FilterFlagConditionOption<BaseQualRecordData>(getParameters().getConditions()));

		addACOption(new FilterNHsamTagOption<BaseQualRecordData>(getParameters().getConditions()));
		addACOption(new FilterNMsamTagOption<BaseQualRecordData>(getParameters().getConditions()));
		
		/*
		for (int conditionIndex = 0; conditionIndex < getParameters().getConditions().size(); ++conditionIndex) {
			addACOption(new MinMAPQConditionOption<BaseQualRecordData>(conditionIndex + 1, getParameters().getConditions().get(conditionIndex)));
			addACOption(new MinBASQConditionOption<BaseQualRecordData>(conditionIndex + 1, getParameters().getConditions().get(conditionIndex)));
			addACOption(new MinCoverageConditionOption<BaseQualRecordData>(conditionIndex + 1, getParameters().getConditions().get(conditionIndex)));
			addACOption(new FilterFlagConditionOption<BaseQualRecordData>(conditionIndex + 1, getParameters().getConditions().get(conditionIndex)));
			
			addACOption(new FilterNHsamTagOption<BaseQualRecordData>(conditionIndex + 1, getParameters().getConditions().get(conditionIndex)));
			addACOption(new FilterNMsamTagOption<BaseQualRecordData>(conditionIndex + 1, getParameters().getConditions().get(conditionIndex)));
		}
		*/
	}
	
	/**
	 * Helper
	 * @return Map of available output formats
	 */
	private Map<Character, AbstractRecordFormat<BaseQualRecordData>> getRecordFormat() {
		final Map<Character, AbstractRecordFormat<BaseQualRecordData>> outputFormats = 
				new HashMap<Character, AbstractRecordFormat<BaseQualRecordData>>();

		// SAM output
		AbstractRecordFormat<BaseQualRecordData> outputFormat = new SAMRecordFormat<BaseQualRecordData>();
		outputFormats.put(outputFormat.getC(), outputFormat);

		// BAM output
		outputFormat = new BAMRecordFormat<BaseQualRecordData>();
		outputFormats.put(outputFormat.getC(), outputFormat);

		// FASTQ output
		outputFormat = new FASTQRecordFormat<BaseQualRecordData>();
		outputFormats.put(outputFormat.getC(), outputFormat);
		
		return outputFormats;
	}

	/**
	 * Helper
	 * @return Map of available output formats
	 */
	private Map<Character, AbstractVariantFormat<BaseQualRecordData>> getVariantFormat() {
		final Map<Character, AbstractVariantFormat<BaseQualRecordData>> outputFormats = 
				new HashMap<Character, AbstractVariantFormat<BaseQualRecordData>>();

		// BEDlike
		AbstractVariantFormat<BaseQualRecordData> outputFormat = new BEDlikeVariantFormat<BaseQualRecordData>();
		outputFormats.put(outputFormat.getC(), outputFormat);

		// VCF
		outputFormat = new VCFVariantFormat<BaseQualRecordData>();
		outputFormats.put(outputFormat.getC(), outputFormat);

		return outputFormats;
	}
	
	@Override
	public boolean parseArgs(String[] args) throws Exception {
		if (args == null || args.length < 1) {
			throw new ParseException("BAM File is not provided!");
		}

		return super.parseArgs(args);
	}
	
	@Override
	public BaseQualRecordData createData() {
		return new BaseQualRecordData();
	}

	@Override
	public BaseQualRecordData[] createContainerData(final int n) {
		return new BaseQualRecordData[n];
	}

	@Override
	public BaseQualRecordData copyData(final BaseQualRecordData dataContainer) {
		return new BaseQualRecordData(dataContainer);
	}
	
	@Override
	public BaseQualRecordData[] copyContainerData(final BaseQualRecordData[] dataContainer) {
		BaseQualRecordData[] ret = createContainerData(dataContainer.length);
		for (int i = 0; i < dataContainer.length; ++i) {
			ret[i] = new BaseQualRecordData(dataContainer[i]);
		}
		return ret;
	}
	
	@Override
	public SAMRecordModifier createRecordModifier() {
		return new RandomMutation<BaseQualRecordData>(getParameters());
	}

}
