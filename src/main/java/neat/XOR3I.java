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
	public float calculateFitness(Genome gnm) {
		List<Float> inList = new ArrayList<Float>();
		inList.add(1f);
		inList.add(1f);
		inList.add(1f);
		float d1 = gnm.calculate(inList).get(0);
		inList.set(0, 0f);
		inList.set(1, 0f);
		inList.set(2, 0f);
		float d2 = gnm.calculate(inList).get(0);
		inList.set(0, 0f);
		inList.set(1, 0f);
		inList.set(2, 1f);
		float d3 = 1f - gnm.calculate(inList).get(0);
		inList.set(0, 0f);
		inList.set(1, 1f);
		inList.set(2, 0f);
		float d4 = 1f - gnm.calculate(inList).get(0);
		inList.set(0, 0f);
		inList.set(1, 1f);
		inList.set(2, 1f);
		float d5 = 1f - gnm.calculate(inList).get(0);
		inList.set(0, 1f);
		inList.set(1, 0f);
		inList.set(2, 0f);
		float d6 = 1f - gnm.calculate(inList).get(0);
		inList.set(0, 1f);
		inList.set(1, 0f);
		inList.set(2, 1f);
		float d7 = 1f - gnm.calculate(inList).get(0);
		inList.set(0, 1f);
		inList.set(1, 1f);
		inList.set(2, 0f);
		float d8 = 1f - gnm.calculate(inList).get(0);
		float d = d1 + d2 + d3 + d4 + d5 + d6 + d7 + d8;
		return (8f - d) * (8f - d);
	}
	@Override
	public float getThreshold() {
		return 63f;
	}
}
