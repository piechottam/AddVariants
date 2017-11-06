package addvariants.cli.parameters;

import jacusa.data.BaseCallConfig;

import java.util.ArrayList;
import java.util.List;

import addvariants.AddVariants;
import addvariants.data.BaseQualRecordData;
import addvariants.io.variant.AbstractVariantFormat;
import addvariants.io.variant.BEDlikeVariantFormat;
import addvariants.method.AbstractMethodFactory;

public abstract class AbstractParameters<T extends BaseQualRecordData> {

	private int activeWindowSize;
	private int reservedWindowSize;
	
	private int threads;
	private BaseCallConfig baseConfig;
	
	// bed file to scan for variants
	private String inputBedFilename;
	
	// chosen method
	private AbstractMethodFactory<T> methodFactory;

	// variant output
	private String variantFilename;
	private AbstractVariantFormat<T> variantFormat;
	
	private List<Condition<T>> conditions;
	
	// debug flag
	private boolean debug;

	public AbstractParameters() {
		activeWindowSize 	= 10000;
		reservedWindowSize	= 10 * activeWindowSize;
		
		threads = 1;
		baseConfig = BaseCallConfig.getInstance();

		conditions = new ArrayList<Condition<T>>(3);

		inputBedFilename	= new String();
		debug				= false;
		
		variantFilename = new String();
		variantFormat = new BEDlikeVariantFormat<T>();
	}
	
	public AbstractParameters(final int conditions) {
		this();
		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			this.conditions.add(new Condition<T>());
		}
	}

	public int getMaxThreads() {
		return threads;
	}
	
	public BaseCallConfig getBaseConfig() {
		return baseConfig;
	}
	
	public List<Condition<T>> getConditions() {
		return conditions;
	}
	
	/**
	 * @return the bedPathname
	 */
	public String getInputBedFilename() {
		return inputBedFilename;
	}

	/**
	 * @param inputBedFilename the bedPathname to set
	 */
	public void setInputBedFilename(final String inputBedFilename) {
		this.inputBedFilename = inputBedFilename;
	}

	/**
	 * @return the methodFactory
	 */
	public AbstractMethodFactory<T> getMethodFactory() {
		return methodFactory;
	}

	/**
	 * @param methodFactory the methodFactory to set
	 */
	public void setMethodFactory(final AbstractMethodFactory<T> methodFactory) {
		this.methodFactory = methodFactory;
	}

	/**
	 * @return the debug
	 */
	public boolean isDebug() {
		return debug;
	}

	public void setMaxThreads(final int threads) {
		this.threads = threads;
	}
	
	public void setActiveWindowSize(final int activeWindowSize) {
		this.activeWindowSize = activeWindowSize;
	}
	
	/**
	 * @param debug the debug to set
	 */
	public void setDebug(final boolean debug) {
		AddVariants.getLogger().addDebug("DEBUG Modus Active!");
		this.debug = debug;
	}
	
	public int getReservedWindowSize() {
		return reservedWindowSize;
	}

	public void setReservedWindowSize(final int reservedWindowSize) {
		this.reservedWindowSize = reservedWindowSize;
	}
	
	public int getActiveWindowSize() {
		return activeWindowSize;
	}

	public AbstractVariantFormat<T> getVariantFormat() {
		return variantFormat;
	}

	public void setVariantFormat(final AbstractVariantFormat<T> variantFormat) {
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
