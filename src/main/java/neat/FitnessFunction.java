package neat;

/**
 * The Interface FitnessFunction. Implement this interface to define the
 * function for the Genome to complete
 */
public interface FitnessFunction {

	public double getThreshold();
	
	public int numInputs();
	
	public int numOutputs();
	
	/**
	 * Calculates the fitness of the input Genome.
	 *
	 * @param gnm
	 *            the gnm
	 */
	public double calculateFitness(Genome gnm);

}
