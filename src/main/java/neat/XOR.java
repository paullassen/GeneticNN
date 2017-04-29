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
	public double calculateFitness(Genome gnm) {
		List<Double> inList = new ArrayList<Double>();
		inList.add(0d);
		inList.add(0d);
		double d1 = gnm.calculate(inList).get(0);
		inList.set(0, 0d);
		inList.set(1, 1d);
		double d2 = 1d - gnm.calculate(inList).get(0);
		inList.set(0, 1d);
		inList.set(1, 0d);
		double d3 = 1d - gnm.calculate(inList).get(0);
		inList.set(0, 1d);
		inList.set(1, 1d);
		double d4 = gnm.calculate(inList).get(0);
		double d = d1 + d2 + d3 + d4;
		return (4d - d) * (4d - d);
	}

	@Override
	public double getThreshold() {
		return 15.9d;
	}
}
