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
	public void calculateFitness(Genome gnm) {
		List<Float> inList = new ArrayList<Float>();
		inList.add(1f);
		inList.add(1f);
		inList.add(1f);
		float d10 = 1f - gnm.calculate(inList).get(0);
		float d11 = 1f - gnm.calculate(inList).get(1);
		inList.set(0, 0f);
		inList.set(1, 0f);
		inList.set(2, 0f);
		float d20 = gnm.calculate(inList).get(0);
		float d21 = gnm.calculate(inList).get(1);
		inList.set(0, 0f);
		inList.set(1, 0f);
		inList.set(2, 1f);
		float d30 = 1f - gnm.calculate(inList).get(0);
		float d31 = gnm.calculate(inList).get(1);
		inList.set(0, 0f);
		inList.set(1, 1f);
		inList.set(2, 0f);
		float d40 = 1f - gnm.calculate(inList).get(0);
		float d41 = gnm.calculate(inList).get(1);
		inList.set(0, 0f);
		inList.set(1, 1f);
		inList.set(2, 1f);
		float d50 = gnm.calculate(inList).get(0);
		float d51 = 1f - gnm.calculate(inList).get(1);
		inList.set(0, 1f);
		inList.set(1, 0f);
		inList.set(2, 0f);
		float d60 = 1f - gnm.calculate(inList).get(0);
		float d61 = gnm.calculate(inList).get(1);
		inList.set(0, 1f);
		inList.set(1, 0f);
		inList.set(2, 1f);
		float d70 = gnm.calculate(inList).get(0);
		float d71 = 1f - gnm.calculate(inList).get(1);
		inList.set(0, 1f);
		inList.set(1, 1f);
		inList.set(2, 0f);
		float d80 = gnm.calculate(inList).get(0);
		float d81 = 1f - gnm.calculate(inList).get(1);
		float d = d10 + d11 + d20 + d21 + d30 + d31 + d40 + d41 + d50 + d51 + d60 + d61 + d70 + d71 + d80 + d81;
		gnm.fitness = (16f - d) * (16f - d);
	}

}
