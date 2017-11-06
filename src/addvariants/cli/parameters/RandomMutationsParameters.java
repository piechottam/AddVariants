package addvariants.cli.parameters;

import addvariants.data.BaseQualRecordData;

public class RandomMutationsParameters<T extends BaseQualRecordData> 
extends AbstractParameters<T> {

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
