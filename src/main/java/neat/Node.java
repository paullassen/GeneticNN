package neat;

import java.util.Map;

/**
 * The Class Node.
 */
class Node implements Comparable<Node> {

	static int count = 0;
	int id;

	boolean input;


	/**
	 * Instantiates a new node.
	 * 
	 * @param mode
	 *            the mode , 0 for input, 1 for anything else
	 */
	public Node(int mode) {
		id = count++;
		input = (mode == 0);
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
	public double calculateNode(Network net, Map<Node, Double> interimMap) {
		if(interimMap.containsKey(this)){
			return interimMap.get(this);
		}

		double value = 0f;
		for (Edge e : net.network.get(this)) {
			value += e.in.calculateNode(net, interimMap) * e.weightMap.get(net);
		}

		value = (double) (1 / (1 + Math.exp(-5 * value)));
		interimMap.put(this, value);
		return interimMap.get(this);
	}
}
