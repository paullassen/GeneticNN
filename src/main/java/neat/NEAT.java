package neat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Class NEAT. This is the Main class
 */
public class NEAT {

	/**
	 * Begin evolving the network.
	 *
	 * @param og
	 *            the population of networks
	 * @param popSize
	 *            the population size
	 * @param maxLoops
	 *            the max number of loops
	 * @return the winning genome
	 */
	public static Genome run(OverGen og, int popSize, int maxLoops) {
		og.createBasePop(popSize);
		List<Genome> top = new ArrayList<Genome>(og.topFit);
		GenomeFitnessComparatorDesc gfc = new GenomeFitnessComparatorDesc();
		Collections.sort(top, gfc);
		int genBT = 0;
		while (top.get(0).fitness < 15.9f) {
			og.populateGeneration();
			top.addAll(og.topFit);
			Collections.sort(top, gfc);
			if (top.get(0).fitness > 9f && genBT == 0) {
				genBT = og.gen;
			}
			if (og.gen >= maxLoops) {
				return null;
			}
		}
		System.out.println("Winning Fitness: " + top.get(0).fitness);
		System.out.print("BreakThrough Generation: " + genBT);
		System.out.print("\tFinal Generation: " + og.gen);
		return top.get(0);
	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		// Two input XOR
		FitnessFunction fitFunc = new XOR();
		// Three input XOR
		// FitnessFunction fitFunc = new XOR3I();
		// sum of 3 [1 bit]-inputs
		// FitnessFunction fitFunc = new ADD3I2O();

		OverGen og = new OverGen(fitFunc);

		Genome gnm = run(og, 100, 500);
		if (gnm != null) {
			gnm.printFloat();
		}
	}
}
