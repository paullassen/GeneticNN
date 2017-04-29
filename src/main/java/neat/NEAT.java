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
		 FitnessFunction fitFunc = new XOR();
		// Three input XOR
		// FitnessFunction fitFunc = new XOR3I();
		// sum of 3 [1 bit]-inputs
		// FitnessFunction fitFunc = new ADD3I2O();

		Population og = new Population(fitFunc);

		Network gnm = og.run(100, 1000);
		if (gnm != null) {
			gnm.printDouble();
		}
	}
}
