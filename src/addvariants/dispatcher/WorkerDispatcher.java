package addvariants.dispatcher;

import jacusa.util.Coordinate;
import jacusa.util.ProgressIndicator;
import jacusa.util.coordinateprovider.CoordinateProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import addvariants.cli.parameters.AbstractParameters;
import addvariants.data.BaseQualRecordData;
import addvariants.io.CopyTmp;
import addvariants.method.AbstractMethodFactory;
import addvariants.worker.Worker;

/**
 * 
 * @author Michael Piechotta
 *
 * @param <T>
 */
public class WorkerDispatcher<T extends BaseQualRecordData> {

	private final AbstractMethodFactory<T> methodFactory;
	private final CoordinateProvider coordinateProvider;
	
	private final List<Worker<T>> workerContainer;
	private final List<Worker<T>> runningWorkers;

	private Integer comparisons;
	private List<Integer> threadIds;
	
	private ProgressIndicator progressIndicator;
	private int current;
	
	public WorkerDispatcher(final AbstractMethodFactory<T> methodFactory, final CoordinateProvider coordinateProvider) {
		this.methodFactory = methodFactory;
		this.coordinateProvider = coordinateProvider;
		
		AbstractParameters<T> parameters = methodFactory.getParameters();
		workerContainer = new ArrayList<Worker<T>>(parameters.getMaxThreads());
		runningWorkers	= new ArrayList<Worker<T>>(parameters.getMaxThreads());

		comparisons 	= 0;
		threadIds		= new ArrayList<Integer>(10000);
		
		progressIndicator = new ProgressIndicator(System.out);
		current			= 0;
	}

	protected Worker<T> createWorker() throws IOException {
		return new Worker<T>(this, workerContainer.size(), 
				methodFactory.getParameters().getVariantFormat(), 
				methodFactory.createRecordModifier());
	}

	public synchronized Coordinate next() {
		current++;
		return coordinateProvider.next();
	}

	public synchronized boolean hasNext() {
		return coordinateProvider.hasNext();
	}

	public int run() throws IOException {
	    final long startTime = System.currentTimeMillis();
	    progressIndicator.print("Implanting variants:");

		while (hasNext() || ! runningWorkers.isEmpty()) {
			for (int i = 0; i < runningWorkers.size(); ++i) {
				final Worker<T> runningWorker = runningWorkers.get(i);
				
				switch (runningWorker.getStatus()) {
				case FINISHED:
					synchronized (comparisons) {
						// TODO comparisons += runningWorker.getSites();
					}
					synchronized (runningWorkers) {
						runningWorkers.remove(runningWorker);
					}
					break;

				default:
					break;
				}
			} 

			synchronized (this) {
				// fill thread container
				while (runningWorkers.size() < methodFactory.getParameters().getMaxThreads() && hasNext()) {
					final Worker<T> worker = createWorker();

					workerContainer.add(worker);
					runningWorkers.add(worker);
					worker.start();
				}

				progressIndicator.update("Progress: ", startTime, current, coordinateProvider.getTotal());
				
				// computation finished
				if (! hasNext() && runningWorkers.isEmpty()) {
					progressIndicator.print("\nDone!\n");
					break;
				}
				try {
					this.wait(2 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		writeOutput();

		return comparisons;
	}
	
	/**
	 * 
	 * @return
	 */

	public List<Worker<T>> getWorkerContainer() {
		return workerContainer;
	}

	public List<Integer> getThreadIds() {
		return threadIds;
	}

	public AbstractMethodFactory<T> getMethodFactory() {
		return methodFactory;
	}
	
	protected void writeOutput() throws IOException {
		progressIndicator.print("Merging files:");
		CopyTmp<T> copyTmp = new CopyTmp<T>(threadIds, methodFactory.getParameters(), workerContainer);
		copyTmp.copy();
		copyTmp.close();
		progressIndicator.print("\nDone!");
	}
	
}
