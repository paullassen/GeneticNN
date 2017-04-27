package neat;

import java.util.ArrayList;
import java.util.List;

/**
 * An XOR with 2 inputs and 1 output
 */
public class XOR implements FitnessFunction {

	@Override
	public int numInputs() {
		return 2;
	}

	@Override
	public int numOutputs() {
		return 1;
	}

	@Override
	public float calculateFitness(Genome gnm) {
		List<Float> inList = new ArrayList<Float>();
		inList.add(0f);
		inList.add(0f);
		float d1 = gnm.calculate(inList).get(0);
		inList.set(0, 0f);
		inList.set(1, 1f);
		float d2 = 1f - gnm.calculate(inList).get(0);
		inList.set(0, 1f);
		inList.set(1, 0f);
		float d3 = 1f - gnm.calculate(inList).get(0);
		inList.set(0, 1f);
		inList.set(1, 1f);
		float d4 = gnm.calculate(inList).get(0);
		float d = d1 + d2 + d3 + d4;
		return (4f - d) * (4f - d);
	}

	@Override
	public float getThreshold() {
		return 15.9f;
	}
}
