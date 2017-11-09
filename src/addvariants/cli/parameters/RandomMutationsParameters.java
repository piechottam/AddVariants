package addvariants.cli.parameters;

import lib.data.AbstractData;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasRecordWrapper;

public class RandomMutationsParameters<T extends AbstractData & hasBaseCallCount & hasRecordWrapper> 
extends AddVariantsParameters<T> {

	private double mutationRate;
	
	public RandomMutationsParameters(final int conditions) {
		super(conditions);

		mutationRate = 0.001;
	}

	public double getMutationRate() {
		return mutationRate;
	}

	public void setMutationRate(final double mutationRate) {
		this.mutationRate = mutationRate;
	}
	
}
