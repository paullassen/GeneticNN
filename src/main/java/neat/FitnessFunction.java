package neat;

/**
 * The Interface FitnessFunction. Implement this interface to define the
 * function for the Genome to complete
 */
public interface FitnessFunction {

	public int numInputs();
	
	public int numOutputs();
	
	/**
	 * Calculates the fitness of the input Genome.
	 *
	 * @param gnm
	 *            the gnm
	 */
	public void calculateFitness(Genome gnm);

}
