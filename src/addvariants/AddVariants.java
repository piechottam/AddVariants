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

import jacusa.util.Logger;
import jacusa.util.SimpleTimer;
import jacusa.util.coordinateprovider.BedCoordinateProvider;
import jacusa.util.coordinateprovider.CoordinateProvider;
import jacusa.util.coordinateprovider.ThreadedCoordinateProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import addvariants.cli.CLI;
import addvariants.cli.parameters.AbstractParameters;
import addvariants.dispatcher.WorkerDispatcher;
import addvariants.method.AbstractMethodFactory;
import addvariants.method.RandomMutationsMethod1;

public class AddVariants {

	// timer used for all time measurements
	private static SimpleTimer timer;
	public static final String NAME = "AddVariants"; 	
	public static final String JAR = NAME + ".jar";
	public static final String VERSION = "0.9-DEVEL"; // System.err.println("version 0.3");
	
	private static Logger logger;
	
	// command line interface
	private CLI cli;

	public static final Character TAG_PREFFIX = 'Z';

	public AddVariants() {
		cli = CLI.getSingleton();

		// container for available methods
		Map<String, AbstractMethodFactory<?>> factories = 
				new TreeMap<String, AbstractMethodFactory<?>>();

		// helper to populate factories
		List<AbstractMethodFactory<?>> tmpFactories = new ArrayList<AbstractMethodFactory<?>>(10);
		tmpFactories.add(new RandomMutationsMethod1());

		for (AbstractMethodFactory<?> factory : tmpFactories) {
			factories.put(factory.getName(), factory);
		}

		// add to cli 
		cli.setMethodFactories(factories);
	}
	
	/**
	 * Singleton Pattern
	 * @return a SimpleTimer instance
	 */
	public static SimpleTimer getSimpleTimer() {
		if (timer == null) {
			timer = new SimpleTimer();
		}

		return timer;
	}

	/**
	 * 
	 * @param line
	 */
	@Deprecated
	public static void printLog(String line) {
		printLine("INFO", line);
	}

	/**
	 * 
	 * @param line
	 */
	@Deprecated
	public static void printWarning(String line) {
		printLine("WARNING", line);
	}
	
	/**
	 * 
	 * @param line
	 */
	@Deprecated
	public static void printDebug(String line) {
		printLine("DEBUG", line);
	}
	
	/**
	 * 
	 * @param id
	 * @param line
	 */
	@Deprecated
	public static void printLine(final String id, final String line) {
		String time = id + "\t" + getSimpleTimer().getTotalTimestring() + " ";
		System.err.println(time + " " + line);
	}

	/**
	 * 
	 * @param args
	 */
	@Deprecated
	private void printProlog(final String[] args) {
		String lineSep = "--------------------------------------------------------------------------------";

		System.err.println(lineSep);

		StringBuilder sb = new StringBuilder();
		sb.append(JAR);
		sb.append(" Version: ");
		sb.append(VERSION);
		for(String arg : args) {
			sb.append(" " + arg);
		}
		System.err.println(sb.toString());
		System.err.println(lineSep);
	}
	
	/**
	 * 
	 * @param sites
	 */
	@Deprecated
	private void printEpilog(final int sites) {
		// print statistics to STDERR
		printLog("Finished implanting variants.");

		System.err.print("Implanted variants can be found in: ");
		String filename = cli.getMethodFactory().getParameters().getVariantFilename();
		System.err.println(filename);
		
		final int conditionsSize = cli.getMethodFactory().getParameters().getConditions().size();
		
		System.err.print("Modified reads/records can be found in: ");
		int conditionIndex = 0;
		filename = cli.getMethodFactory().getParameters().getConditions().get(conditionIndex).getRecordFilename();
		System.err.println(filename);
		conditionIndex++;
		for (; conditionIndex < conditionsSize; conditionIndex++) {
			filename = cli.getMethodFactory().getParameters()
					.getConditions().get(conditionIndex)
					.getRecordFilename();
			System.err.println(", " + filename);
			
		}
		final String lineSep = "--------------------------------------------------------------------------------";
		System.err.println(lineSep);
		//System.err.println("Processed reads:\t" + sites);
		System.err.println("Elapsed time:\t\t" + getSimpleTimer().getTotalTimestring());
	}
	
	/**
	 * 
	 * @return
	 */
	public CLI getCLI() {
		return cli;
	}
	
	public static Logger getLogger() {
		return logger;
	}
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	private void run(final String[] args) throws Exception {
		// prolog
		printProlog(args);

		// create command line interface (CLI)
		final CLI cmd = getCLI();
		// parse CLI
		if (! cmd.processArgs(args)) {
			System.exit(1);
		}
		
		// instantiate chosen method
		AbstractMethodFactory<?> methodFactory = cmd.getMethodFactory();
		// get method specific parameters
		AbstractParameters<?> parameters = methodFactory.getParameters();
	
		// process coordinate provider
		// use BED file or header of SAM/BAM files
		// to traverse
		CoordinateProvider coordinateProvider = null;
		if (parameters.getInputBedFilename().isEmpty()) { // use header of SAM/BAM files
			methodFactory.initCoordinateProvider();
			coordinateProvider = methodFactory.getCoordinateProvider();
		} else { // use BED file
			coordinateProvider = new BedCoordinateProvider(parameters.getInputBedFilename());
		}

		// pathnames of SAM/BAM files
		final String[][] pathnames = new String[parameters.getConditions().size()][]; 
		for (int conditionIndex = 0; conditionIndex < parameters.getConditions().size(); conditionIndex++) {
			pathnames[conditionIndex] = new String[1];
			pathnames[conditionIndex][0] = parameters.getConditions().get(conditionIndex).getInputFilename();
		}
	
		// wrap chosen coordinate provider enabling multiple thread(s) 
		if (parameters.getMaxThreads() > 1) {
			coordinateProvider = new ThreadedCoordinateProvider(coordinateProvider, pathnames, parameters.getReservedWindowSize());
		}
		
		// run the method...
		final WorkerDispatcher<?> workerDispatcher = methodFactory.createWorkerDispatcher(coordinateProvider);
		final int sites = workerDispatcher.run();
		
		// epilog
		printEpilog(sites);
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		final AddVariants addVarians = new AddVariants();
		addVarians.run(args);
	}

}
