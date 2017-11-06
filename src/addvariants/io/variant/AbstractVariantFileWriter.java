package addvariants.io.variant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import addvariants.data.BaseQualRecordData;

/**
 * 
 * @author Michael Piechotta
 *
 */
public abstract class AbstractVariantFileWriter<T extends BaseQualRecordData> 
extends AbstractVariantWriter<T> {

	private BufferedWriter bw;
	
	public AbstractVariantFileWriter(final String filename, final AbstractVariantFormat<T> format) {
		super(filename, format);
		try {
			bw = new BufferedWriter(new FileWriter(new File(filename)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Helper
	 * @param s Add one line "s" to BufferedWriter
	 */
	protected void addLine(final String s) {
		try {
			bw.write(s + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void close() {
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
