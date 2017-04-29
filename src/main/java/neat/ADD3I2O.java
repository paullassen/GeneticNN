package neat;

import java.util.ArrayList;
import java.util.List;

public class ADD3I2O implements FitnessFunction {

	@Override
	public int numInputs() {
		return 3;
	}

	@Override
	public int numOutputs() {
		return 2;
	}

	@Override
	public double calculateFitness(Genome gnm) {
		List<Double> inList = new ArrayList<Double>();
		inList.add(1d);
		inList.add(1d);
		inList.add(1d);
		double d10 = 1d - gnm.calculate(inList).get(0);
		double d11 = 1d - gnm.calculate(inList).get(1);
		inList.set(0, 0d);
		inList.set(1, 0d);
		inList.set(2, 0d);
		double d20 = gnm.calculate(inList).get(0);
		double d21 = gnm.calculate(inList).get(1);
		inList.set(0, 0d);
		inList.set(1, 0d);
		inList.set(2, 1d);
		double d30 = 1d - gnm.calculate(inList).get(0);
		double d31 = gnm.calculate(inList).get(1);
		inList.set(0, 0d);
		inList.set(1, 1d);
		inList.set(2, 0d);
		double d40 = 1d - gnm.calculate(inList).get(0);
		double d41 = gnm.calculate(inList).get(1);
		inList.set(0, 0d);
		inList.set(1, 1d);
		inList.set(2, 1d);
		double d50 = gnm.calculate(inList).get(0);
		double d51 = 1d - gnm.calculate(inList).get(1);
		inList.set(0, 1d);
		inList.set(1, 0d);
		inList.set(2, 0d);
		double d60 = 1d - gnm.calculate(inList).get(0);
		double d61 = gnm.calculate(inList).get(1);
		inList.set(0, 1d);
		inList.set(1, 0d);
		inList.set(2, 1d);
		double d70 = gnm.calculate(inList).get(0);
		double d71 = 1d - gnm.calculate(inList).get(1);
		inList.set(0, 1d);
		inList.set(1, 1d);
		inList.set(2, 0d);
		double d80 = gnm.calculate(inList).get(0);
		double d81 = 1d - gnm.calculate(inList).get(1);
		double d = d10 + d11 + d20 + d21 + d30 + d31 + d40 + d41 + d50 + d51 + d60 + d61 + d70 + d71 + d80 + d81;
		return (16d - d) * (16d - d);
	}
	public double getThreshold() {
		return 225d;
	}
}
