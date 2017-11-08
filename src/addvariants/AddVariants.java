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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lib.method.AbstractMethodFactory;
import lib.util.AbstractTool;
import lib.util.Logger;
import lib.util.SimpleTimer;
import lib.util.coordinateprovider.BedCoordinateProvider;
import lib.util.coordinateprovider.CoordinateProvider;

import addvariants.cli.parameters.AbstractParameters;
import addvariants.method.RandomMutationsMethod1;

public class AddVariants extends AbstractTool {

	private int sites;

	public static final Character TAG_PREFFIX = 'Z';

	public AddVariants(final String[] args) {
		super("AddVariants2", "0.9-DEVEL", args);
		sites = 0;
	}

	@Override
	protected Map<String, AbstractMethodFactory<?>> getMethodFactories() {
		// container for available methods
		final Map<String, AbstractMethodFactory<?>> methodFactories = 
				new TreeMap<String, AbstractMethodFactory<?>>();

		// helper to populate factories
		final List<AbstractMethodFactory<?>> factories = new ArrayList<AbstractMethodFactory<?>>(10);
		factories.add(new RandomMutationsMethod1());

		for (final AbstractMethodFactory<?> factory : factories) {
			methodFactories.put(factory.getName(), factory);
		}

		return methodFactories;
	}
	
	@Override
	protected String addEpilog() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Finished implanting variants.");
		sb.append('\n');
		
		sb.append("Implanted variants can be found in: ");
		String filename = getCLI().getMethodFactory().getParameters().getVariantFilename();
		sb.append(filename);
		sb.append('\n');
		
		final int conditionsSize = getCLI().getMethodFactory().getParameters().getConditionsSize();
		
		sb.append("Modified reads/records can be found in: ");
		int conditionIndex = 0;
		filename = getCLI().getMethodFactory().getParameters().getConditionParameters().get(conditionIndex).getRecordFilename();
		sb.append(filename);
		conditionIndex++;
		for (; conditionIndex < conditionsSize; conditionIndex++) {
			filename = getCLI().getMethodFactory().getParameters()
					.getConditionParameters().get(conditionIndex)
					.getRecordFilename();
			sb.append(", " + filename);
			
		}
		sb.append('\n');

		final String lineSep = "--------------------------------------------------------------------------------";
		sb.append(lineSep);
		sb.append('\n');
		sb.append("Elapsed time:\t\t" + getLogger().getTimer().getTotalTimestring());
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
