package addvariants.cli.parameters;


import java.util.ArrayList;
import java.util.List;

import lib.data.BaseCallConfig;
import lib.io.variant.AbstractVariantFormat;
import lib.io.variant.BEDlikeVariantFormat;

import addvariants.AddVariants;
import addvariants.data.BaseQualRecordData;
import addvariants.method.AbstractMethodFactory;

public abstract class AbstractParameters<T extends BaseQualRecordData> 
extends lib.cli.parameters.AbstractParameters<T> {
	
	// bed file to scan for variants
	private String inputBedFilename;

	// chosen method
	private AbstractMethodFactory<T> methodFactory;

	// variant output
	private String variantFilename;
	private AbstractVariantFormat variantFormat;
	
	private List<ConditionParameter<T>> conditions;

	public AbstractParameters() {
		super();

		conditions = new ArrayList<ConditionParameter<T>>(3);
		
		variantFilename = new String();
		variantFormat = new BEDlikeVariantFormat<T>();
	}
	
	public AbstractParameters(final int conditionSize) {
		this();
		for (int conditionIndex = 0; conditionIndex < conditionSize; conditionIndex++) {
			this.conditions.add(new ConditionParameter<T>());
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
	
}
