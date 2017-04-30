package neat;

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

		Population og = new Population(fitFunc);

		Network network = og.run(200, 500);
		if (network != null) {
			network.printDouble();
		}

		fitFunc.drawNet(network);
	}

}
