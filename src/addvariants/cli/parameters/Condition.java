package addvariants.cli.parameters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import addvarians.cli.options.condition.filter.samtag.SamTagFilter;
import addvariants.data.BaseQualRecordData;
import addvariants.io.record.AbstractRecordFormat;
import addvariants.io.record.BAMRecordFormat;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMValidationError;

public class Condition<T extends BaseQualRecordData> {

	private byte minBASQ;
	private int minMAPQ;

	private int minCoverage;
	
	// filter: flags
	private int filterFlags;
	private int retainFlags;

	// filter based on SAM tags
	private List<SamTagFilter> samTagFilters;
	
	// path to BAM files
	private String inputFilename;
	private List<SAMFileReader> readers;
	
	// record output
	private String recordFilename;
	private AbstractRecordFormat<T> recordFormat;
	
	private boolean adjustSAMTag;
	
	public Condition() {
		minBASQ	= Byte.parseByte("20");
		minMAPQ	= 20;
		
		minCoverage = 1;
		
		filterFlags	= 0;
		retainFlags	= 0;

		samTagFilters = new ArrayList<SamTagFilter>();
		
		inputFilename = new String();
		readers = new ArrayList<SAMFileReader>(10);
		
		recordFilename = new String();
		recordFormat = new BAMRecordFormat<T>();
		
		adjustSAMTag = true;
	}
	
	/**
	 * @return the minBASQ
	 */
	public byte getMinBASQ() {
		return minBASQ;
	}

	/**
	 * @param minBASQ the minBASQ to set
	 */
	public void setMinBASQ(byte minBASQ) {
		this.minBASQ = minBASQ;
	}

	/**
	 * @return the minMAPQ
	 */
	public int getMinMAPQ() {
		return minMAPQ;
	}

	/**
	 * @param minMAPQ the minMAPQ to set
	 */
	public void setMinMAPQ(int minMAPQ) {
		this.minMAPQ = minMAPQ;
	}
	
	/**
	 * @return the filterFlags
	 */
	public int getFilterFlags() {
		return filterFlags;
	}

	/**
	 * @param filterFlags the filterFlags to set
	 */
	public void setFilterFlags(int filterFlags) {
		this.filterFlags = filterFlags;
	}

	/**
	 * @return the retainFlags
	 */
	public int getRetainFlags() {
		return retainFlags;
	}

	/**
	 * @param retainFlags the retainFlags to set
	 */
	public void setRetainFlags(int retainFlags) {
		this.retainFlags = retainFlags;
	}

	/**
	 * @return the samTagFilters
	 */
	public List<SamTagFilter> getSamTagFilters() {
		return samTagFilters;
	}

	/**
	 * @param samTagFilters the samTagFilters to set
	 */
	public void setSamTagFilters(List<SamTagFilter> samTagFilters) {
		this.samTagFilters = samTagFilters;
	}
	
	/**
	 * @return the pathnames
	 */
	public String getInputFilename() {
		return inputFilename;
	}

	/**
	 * @param pathnames the pathnames to set
	 */
	public void setInputFilename(final String inputFilename) {
		this.inputFilename = inputFilename;
	}

	public List<SAMFileReader> getSAMFileReader() {
		return readers;
	}
	
	public SAMFileReader createSAMFileReader() {
		final File file = new File(inputFilename);
		final SAMFileReader reader = new SAMFileReader(file);
		reader.setValidationStringency(SAMFileReader.ValidationStringency.LENIENT);
		// disable memory mapping
		reader.enableIndexMemoryMapping(false);
		readers.add(reader);

		return reader;
	}
	

	public boolean isValid(final SAMRecord record) {
		int mapq = record.getMappingQuality();
		List<SAMValidationError> errors = record.isValid();

		if (! record.getReadUnmappedFlag()
				&& ! record.getNotPrimaryAlignmentFlag() // ignore non-primary alignments CHECK
				&& (mapq < 0 || mapq >= getMinMAPQ()) // filter by mapping quality
				&& (getFilterFlags() == 0 || (getFilterFlags() > 0 && ((record.getFlags() & getFilterFlags()) == 0)))
				&& (getRetainFlags() == 0 || (getRetainFlags() > 0 && ((record.getFlags() & getRetainFlags()) > 0)))
				&& errors == null // isValid is expensive
				) { // only store valid records that contain mapped reads
			// custom filter 
			for (SamTagFilter samTagFilter : getSamTagFilters()) {
				if (samTagFilter.filter(record)) {
					return false;
				}
			}

			// no errors found
			return true;
		}

		// print error messages
		if (errors != null) {
			for (SAMValidationError error : errors) {
				 System.err.println(error.toString());
			}
		}

		// something went wrong
		return false;
	}

	public int getMinCoverage() {
		return minCoverage;
	}

	public void setMinCoverage(final int minCoverage) {
		this.minCoverage = minCoverage;
	}

	public AbstractRecordFormat<T> getRecordFormat() {
		return recordFormat;
	}

	public void setRecordFormat(final AbstractRecordFormat<T> recordFormat) {
		this.recordFormat = recordFormat;
	}

	
	public void setRecordFilename(final String recordFilename) {
		this.recordFilename = recordFilename;
	}
	
	public String getRecordFilename() {
		if (recordFilename.isEmpty()) { // default
			return inputFilename.substring(inputFilename.lastIndexOf(".")) + 
					"_mutated." + getRecordFormat().getSuffix();
		}

		return recordFilename;
	}
	
	public boolean adjustSAMTag() {
		return adjustSAMTag;
	}
	
	public void setAdjustSAMTag(final boolean adjustSAMTag) {
		this.adjustSAMTag = adjustSAMTag;
	}
	
}
