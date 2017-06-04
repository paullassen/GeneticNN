package neat;

import java.io.IOException;

/**
 * The Class NEAT. This is the Main class
 */
public class NEAT {
	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		// Two input XOR
		// FitnessFunction fitFunc = new XOR();
		// Three input XOR
		// FitnessFunction fitFunc = new XOR3I();
		// sum of 3 [1 bit]-inputs
		// FitnessFunction fitFunc = new ADD2I2O();

		 InvertedPendulumOnCart fitFunc = new InvertedPendulumOnCart();

		Population pop = new Population(fitFunc);

		Network network = pop.run(200, 2000);
		if (network != null) {
			network.printDouble();
		}
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 fitFunc.drawNet(pop.topFitness());
	}

}
 