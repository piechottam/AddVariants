package addvariants.cli.parameters;

import java.util.ArrayList;

import java.util.List;

import addvariants.io.variant.AbstractVariantFormat;
import addvariants.io.variant.BEDlikeVariantFormat;

import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.builder.factory.AbstractDataBuilderFactory;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasRecordWrapper;

public class AddVariantsParameters<T extends AbstractData & hasBaseCallCount & hasRecordWrapper> 
extends AbstractParameter<T, ?> {

	// variant output
	private String variantFilename;
	private AbstractVariantFormat variantFormat;
	
	private List<AddVariantsConditionParameter<T>> conditionParameter;

	public AddVariantsParameters() {
		super();

		conditionParameter = new ArrayList<AddVariantsConditionParameter<T>>(3);
		
		variantFilename = new String();
		variantFormat = new BEDlikeVariantFormat();
	}
	
	public AddVariantsParameters(final int conditionSize) {
		this();
		for (int conditionIndex = 0; conditionIndex < conditionSize; conditionIndex++) {
			this.conditionParameter.add(new AddVariantsConditionParameter<T>());
		}
	}

	public AbstractVariantFormat getVariantFormat() {
		return variantFormat;
	}

	public void setVariantFormat(final AbstractVariantFormat variantFormat) {
		this.variantFormat = variantFormat;
	}
	
	public String getVariantFilename() {
		if (variantFilename.isEmpty()) { // default
			return "variants." + getVariantFormat().getSuffix();
		}
		
		return variantFilename;
	}
	
	public void setVariantFilename(final String variantFilename) {
		this.variantFilename = variantFilename;
	}

	@Override
	public AbstractConditionParameter<T> createConditionParameter(
			final AbstractDataBuilderFactory<T> dataBuilderFactory) {
		final AddVariantsConditionParameter<T> conditionParameter = new AddVariantsConditionParameter<T>();
		conditionParameter.setDataBuilderFactory(dataBuilderFactory);
		return conditionParameter;
	}
	
	@Override
	public AddVariantsConditionParameter<T> getConditionParameter(final int conditionIndex) {
		return (AddVariantsConditionParameter<T>) super.getConditionParameter(conditionIndex);
	}
	
}
