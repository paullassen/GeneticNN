package neat;

/**
 * The Class Node.
 */
class Node implements Comparable<Node> {

	static int count = 0;
	int id;
	static OverGen og;
	boolean bias, input, output;

	boolean calculated;

	/** The Node Output. */
	public float value = 0f;

	/**
	 * Instantiates a new node. Use to create bias node. Sets OverGen for all
	 * future Nodes
	 *
	 * @param ovG
	 *            the ov G
	 */
	public Node(OverGen ovG) {
		Node.og = ovG;
		id = count++;
		bias = true;
		input = false;
		output = false;
		value = 1f;
		calculated = true;
	}

	/**
	 * Instantiates a new node. Use to create input/output nodes
	 * 
	 * @param mode
	 *            the mode , 0 for input, 1 for output
	 */
	public Node(int mode) {
		// Use to create input/output nodes
		id = count++;
		bias = false;
		input = (mode == 0);
		output = (mode == 1);
		calculated = input;
	}

	/**
	 * Instantiates a new node.
	 */
	public Node() {
		id = count++;
		bias = false;
		input = false;
		output = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
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
