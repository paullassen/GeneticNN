package neat;
import java.util.ArrayList;
import java.util.List;

/**
 * An XOR with 3 inputs, and 1 output
 */
public class XOR3I implements FitnessFunction{

	@Override
	public int numInputs() {
		return 3;
	}

	@Override
	public int numOutputs() {
		return 1;
	}

	@Override
	public double calculateFitness(Genome gnm) {
		List<Double> inList = new ArrayList<Double>();
		inList.add(1d);
		inList.add(1d);
		inList.add(1d);
		double d1 = gnm.calculate(inList).get(0);
		inList.set(0, 0d);
		inList.set(1, 0d);
		inList.set(2, 0d);
		double d2 = gnm.calculate(inList).get(0);
		inList.set(0, 0d);
		inList.set(1, 0d);
		inList.set(2, 1d);
		double d3 = 1d - gnm.calculate(inList).get(0);
		inList.set(0, 0d);
		inList.set(1, 1d);
		inList.set(2, 0d);
		double d4 = 1d - gnm.calculate(inList).get(0);
		inList.set(0, 0d);
		inList.set(1, 1d);
		inList.set(2, 1d);
		double d5 = 1d - gnm.calculate(inList).get(0);
		inList.set(0, 1d);
		inList.set(1, 0d);
		inList.set(2, 0d);
		double d6 = 1d - gnm.calculate(inList).get(0);
		inList.set(0, 1d);
		inList.set(1, 0d);
		inList.set(2, 1d);
		double d7 = 1d - gnm.calculate(inList).get(0);
		inList.set(0, 1d);
		inList.set(1, 1d);
		inList.set(2, 0d);
		double d8 = 1d - gnm.calculate(inList).get(0);
		double d = d1 + d2 + d3 + d4 + d5 + d6 + d7 + d8;
		return (8d - d) * (8d - d);
	}
	@Override
	public double getThreshold() {
		return 63d;
	}
}
