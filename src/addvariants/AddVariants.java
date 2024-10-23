/*

  	AddVariants implants variants into BAM files.
    Copyright (C) 2015  Michael Piechotta

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/

package addvariants;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lib.method.AbstractMethodFactory;
import lib.util.AbstractTool;

public class AddVariants extends AbstractTool {

	public static final Character TAG_PREFFIX = 'Z';

	public AddVariants(final String[] args) {
		super("AddVariants2", "0.91-DEVEL", args);
	}

	@Override
	protected Map<String, AbstractMethodFactory<?, ?>> getMethodFactories() {
		// container for available methods
		final Map<String, AbstractMethodFactory<?, ?>> methodFactories = 
				new LinkedHashMap<String, AbstractMethodFactory<?, ?>>();

		// helper to populate factories
		final List<AbstractMethodFactory<?, ?>> tmpMethodFactories = new ArrayList<AbstractMethodFactory<?, ?>>(2);
		// FIXME methodFactories.add(new RandomMutationsMethod1());

		for (final AbstractMethodFactory<?, ?> methodFactory : tmpMethodFactories) {
			methodFactories.put(methodFactory.getName(), methodFactory);
		}

		return methodFactories;
	}
	
	@Override
	protected String getEpilog() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Finished implanting variants.");
		sb.append('\n');
		
		/* TODO
		sb.append("Implanted variants can be found in: ");
		final String filename = getCLI().getMethodFactory().getParameter().getVariantFilename();
		sb.append(filename);
		sb.append('\n');
		
		final int conditionsSize = getCLI().getMethodFactory().getParameter().getConditionsSize();
		
		sb.append("Modified reads/records can be found in: ");
		int conditionIndex = 0;
		filename = getMethodFactory().getParameter().getConditionParameters().get(conditionIndex).getRecordFilename();
		sb.append(filename);
		conditionIndex++;
		for (; conditionIndex < conditionsSize; conditionIndex++) {
			filename = getCLI().getMethodFactory().getParameter()
					.getConditionParameters().get(conditionIndex)
					.getRecordFilename();
			sb.append(", " + filename);
			
		}
		sb.append('\n');
		*/

		final String lineSep = "--------------------------------------------------------------------------------";
		sb.append(lineSep);
		sb.append('\n');
		sb.append("Elapsed time:\t\t" + getLogger().getTimer().getTotalTimestring());
		return sb.toString();
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		final AddVariants addVarians = new AddVariants(args);
		addVarians.run();
	}

}
