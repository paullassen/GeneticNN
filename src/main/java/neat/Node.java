package neat;

/**
 * The Class Node.
 */
class Node implements Comparable<Node> {

	static int count = 0;
	int id;

	boolean input;

	boolean calculated;

	/** The Node Output. */
	public float value = 0f;

	/**
	 * Instantiates a new node.
	 * 
	 * @param mode
	 *            the mode , 0 for input, 1 for anything else
	 */
	public Node(int mode) {
		id = count++;
		input = (mode == 0);
		calculated = input;
	}

	@Override
	public int compareTo(Node arg0) {
		return this.id - arg0.id;
	}

	/**
	 * Calculate the value of this Node for Genome gnm.
	 *
	 * @param gnm
	 *            the gnm
	 * @param depth
	 *            the depth
	 * @return the calulated value of this node
	 */
	public float calculateNode(Genome gnm) {
		if (calculated) {
			return value;
		}
		float tmp = 0f;
		for (Gene g : gnm.genome.get(this)) {
			tmp += g.in.calculateNode(gnm) * g.weightMap.get(gnm);
		}

		calculated = true;
		value = (float) (1 / (1 + Math.exp(-5 * tmp)));
		return value;
	}
}
