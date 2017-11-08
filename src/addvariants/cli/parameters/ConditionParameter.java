package addvariants.cli.parameters;

import lib.cli.parameters.AbstractConditionParameter;
import lib.io.record.AbstractRecordFormat;
import lib.io.record.BAMRecordFormat;

import addvariants.data.BaseQualRecordData;

public class ConditionParameter<T extends BaseQualRecordData> 
extends AbstractConditionParameter<T> {
	
	// record output
	private String recordFilename;
	private AbstractRecordFormat recordFormat;
	
	private boolean adjustSAMTag;
	
	public ConditionParameter() {
		super();
		
		recordFilename = new String();
		recordFormat = new BAMRecordFormat();

		adjustSAMTag = true;
	}
	
	public AbstractRecordFormat getRecordFormat() {
		return recordFormat;
	}

	public void setRecordFormat(final AbstractRecordFormat recordFormat) {
		this.recordFormat = recordFormat;
	}

	
	public void setRecordFilename(final String recordFilename) {
		this.recordFilename = recordFilename;
	}
	
	public String getRecordFilename() {
		/* TODO
		if (recordFilename.isEmpty()) { // default
			return inputFilename.substring(inputFilename.lastIndexOf(".")) + 
					"_mutated." + getRecordFormat().getSuffix();
		}
		*/

		return recordFilename;
	}
	
	public boolean adjustSAMTag() {
		return adjustSAMTag;
	}
	
	public void setAdjustSAMTag(final boolean adjustSAMTag) {
		this.adjustSAMTag = adjustSAMTag;
	}

}
