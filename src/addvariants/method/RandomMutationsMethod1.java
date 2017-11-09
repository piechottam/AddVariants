package addvariants.method;

import java.util.HashMap;
import java.util.Map;

import lib.data.AbstractData;
import lib.io.record.AbstractRecordFormat;
import lib.io.record.BAMRecordFormat;
import lib.io.record.FASTQRecordFormat;
import lib.io.record.SAMRecordFormat;
import lib.io.variant.AbstractVariantFormat;
import lib.io.variant.BEDlikeVariantFormat;
import lib.io.variant.VCFVariantFormat;
import lib.tmp.SAMRecordModifier;

import org.apache.commons.cli.ParseException;

import addvariants.cli.options.MutationRateOption;
import addvariants.cli.options.condition.RecordFilenameOption;
import addvariants.cli.options.condition.RecordFormatOption;
import addvariants.cli.options.condition.VariantFilenameOption;
import addvariants.cli.options.condition.VariantFormatOption;
import addvariants.cli.parameters.AddVariantsConditionParameter;
import addvariants.cli.parameters.RandomMutationsParameters;
import addvariants.data.BaseCallRecordWrapperData;
import addvariants.worker.RandomMutation;

public class RandomMutationsMethod1 
extends AbstractAddVariantsMethodFactory<BaseCallRecordWrapperData> {

	public RandomMutationsMethod1() {
		super("mutate-1", "Add mutatations to 1 SAM/BAM file", new RandomMutationsParameters<BaseCallRecordData>(1));
	}
	
	@Override
	public RandomMutationsParameters<BaseCallRecordWrapperData> getParameter() {
		return (RandomMutationsParameters<BaseCallRecordWrapperData>) super.getParameter();
	}
	
	@Override
	public void initACOptions() {
		initGlobalACOptions();
		initConditionACOptions();
	}

	private void initGlobalACOptions() {
		addACOption(new MaxThreadOption(getParameter()));
		addACOption(new WindowSizeOption(getParameter()));
		addACOption(new ThreadWindowSizeOption(getParameter()));
		addACOption(new HelpOption(CLI.getSingleton()));

		addACOption(new MutationRateOption(getParameter()));
	}
	
	private void initConditionACOptions() {
		// result format
		if (getRecordFormat().size() == 1 ) {
			final Character[] a = getRecordFormat().keySet().toArray(new Character[1]);
			for (final AddVariantsConditionParameter<BaseCallRecordWrapperData> condition : getParameter().getConditionsSize()) {
				condition.setRecordFormat(getRecordFormat().get(a[0]));
			}
		} else {
			addACOption(new RecordFormatOption<BaseCallRecordWrapperData>(getParameter(), getRecordFormat()));
		}
		addACOption(new RecordFilenameOption<BaseCallRecordWrapperData>(getParameter().getConditionsSize()));
		
		// variant format
		if (getVariantFormat().size() == 1 ) {
			final Character[] a = getVariantFormat().keySet().toArray(new Character[1]);
			getParameter().setVariantFormat(getVariantFormat().get(a[0]));
		} else {
			addACOption(new VariantFormatOption<BaseCallRecordWrapperData>(getParameter(), getVariantFormat()));
		}
		addACOption(new VariantFilenameOption(getParameter()));

		addACOption(new MinMAPQConditionOption<BaseCallRecordWrapperData>(getParameter().getConditionsSize()));
		addACOption(new MinBASQConditionOption<BaseCallRecordWrapperData>(getParameter().getConditionsSize()));
		addACOption(new MinCoverageConditionOption<BaseCallRecordWrapperData>(getParameter().getConditionsSize()));
		addACOption(new FilterFlagConditionOption<BaseCallRecordWrapperData>(getParameter().getConditionsSize()));

		addACOption(new FilterNHsamTagOption<BaseCallRecordWrapperData>(getParameter().getConditionsSize()));
		addACOption(new FilterNMsamTagOption<BaseCallRecordWrapperData>(getParameter().getConditionsSize()));
		
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
	private Map<Character, AbstractRecordFormat> getRecordFormat() {
		final Map<Character, AbstractRecordFormat> outputFormats = 
				new HashMap<Character, AbstractRecordFormat>();

		// SAM output
		AbstractRecordFormat outputFormat = new SAMRecordFormat();
		outputFormats.put(outputFormat.getC(), outputFormat);

		// BAM output
		outputFormat = new BAMRecordFormat();
		outputFormats.put(outputFormat.getC(), outputFormat);

		// FASTQ output
		outputFormat = new FASTQRecordFormat();
		outputFormats.put(outputFormat.getC(), outputFormat);
		
		return outputFormats;
	}

	/**
	 * Helper
	 * @return Map of available output formats
	 */
	private Map<Character, AbstractVariantFormat> getVariantFormat() {
		final Map<Character, AbstractVariantFormat> outputFormats = 
				new HashMap<Character, AbstractVariantFormat>();

		// BEDlike
		AbstractVariantFormat outputFormat = new BEDlikeVariantFormat();
		outputFormats.put(outputFormat.getC(), outputFormat);

		// VCF
		outputFormat = new VCFVariantFormat();
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
	public BaseCallRecordWrapperData createData() {
		return new BaseCallRecordWrapperData();
	}

	@Override
	public BaseCallRecordWrapperData[] createContainerData(final int n) {
		return new BaseCallRecordWrapperData[n];
	}

	@Override
	public BaseCallRecordWrapperData copyData(final BaseCallRecordWrapperData data) {
		return new BaseCallRecordWrapperData(data);
	}
	
	@Override
	public BaseCallRecordWrapperData[] copyContainerData(final BaseCallRecordWrapperData[] data) {
		BaseCallRecordWrapperData[] ret = createContainerData(data.length);
		for (int i = 0; i < data.length; ++i) {
			ret[i] = new BaseCallRecordWrapperData(data[i]);
		}
		return ret;
	}
	
	/* TODO
	@Override
	public SAMRecordModifier createRecordModifier() {
		return new RandomMutation<BaseCallRecordData>(getParameter());
	}
	*/

}
