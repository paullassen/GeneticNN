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
	public double value = 0f;

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
	 * Calculate the value of this Node for Network net.
	 *
	 * @param net
	 *            the net
	 * @param depth
	 *            the depth
	 * @return the calulated value of this node
	 */
	public double calculateNode(Network net) {
		if (calculated) {
			return value;
		}
		double tmp = 0f;
		for (Edge e : net.network.get(this)) {
			tmp += e.in.calculateNode(net) * e.weightMap.get(net);
		}

		calculated = true;
		value = (double) (1 / (1 + Math.exp(-5 * tmp)));
		return value;
	}
}
