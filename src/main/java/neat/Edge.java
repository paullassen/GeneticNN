package neat;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

/**
 * Edges function as edges of a network. Edges are static, so all of the Networks
 * share from the same pool; Edges maintain maps of Network specific variables,
 * their weights and states (enabled/disabled);
 */
class Edge implements Comparable<Edge> {

	static int count = 0;

	final int id;

	final Node in, out;

	private final Random rand;

	/**
	 * The weight map contains a double for each Network in which is is contained
	 */
	HashMap<Network, Double> weightMap = new HashMap<Network, Double>();

	/**
	 * The state map contains a boolean (representing enabled or disabled) for
	 * each Network in which it is contained
	 */
	HashMap<Network, Boolean> stateMap = new HashMap<Network, Boolean>();

	/** This is the */
	Node disabler = null;

	/**
	 * Instantiates a new edge.
	 *
	 * @param inp
	 *            the input Node
	 * @param outp
	 *            the output Node
	 * @param rand
	 *            the Random Object
	 */
	public Edge(Node inp, Node outp, Random rand) {
		id = count++;
		in = inp;
		out = outp;
		this.rand = rand;
	}

	public int getId() {
		return id;
	}

	public double getWeight(Network n) {
		return weightMap.get(n);
	}

	/**
	 * Change weight for the specified network by a random amount.
	 *
	 * @param n
	 *            the Network
	 */
	public void changeWeight(Network n) {
		double weight = weightMap.get(n).doubleValue();
		weight = weight + rand.nextDouble() * 4 - 2d;
		weightMap.replace(n, weight);
	}

	public Node getDisabler() {
		return disabler;
	}

	/**
	 * Adds this edge to a Network, with the same weight and state of another
	 * Network.
	 *
	 * @param from
	 *            the Network from which the Edge is copied
	 * @param to
	 *            the Network to which the Edge is copied
	 */
	public void copyToNetwork(Network from, Network to) {
		weightMap.put(to, weightMap.get(from));
		stateMap.put(to, stateMap.get(from));
	}

	/**
	 * Adds this Edge to a Network, with a random weight;
	 * 
	 * @param n
	 *            the Network to which the Edge is added
	 */
	public void addNetwork(Network n) {
		double weight = rand.nextDouble() * 4 - 2;
		weightMap.put(n, weight);
		this.enable(n);
	}

	public boolean getState(Network n) {
		return stateMap.get(n);
	}

	/**
	 * Disable this Edge in the specified Network. Use this the first time Edge
	 * is disabled. The disabler is set to n.
	 *
	 * @param net
	 *            the Network
	 * @param n
	 *            the Disabling Node
	 */
	public void disable(Network net, Node n) {
		stateMap.put(net, false);
		disabler = n;
	}

	/**
	 * Disable this edge for Network g
	 *
	 * @param n
	 *            the Network
	 */
	public void disable(Network n) {
		stateMap.put(n, false);
		// enabled = false;
	}

	/**
	 * Enables this Edge for the Network
	 *
	 * @param n
	 *            the Network
	 */
	public void enable(Network n) {
		stateMap.put(n, true);
	}

	@Override
	public int compareTo(Edge arg0) {
		// Compare by id
		return this.getId() - arg0.getId();
	}
}

class EdgeOutComparator implements Comparator<Edge> {
	@Override
	public int compare(Edge o1, Edge o2) {
		return o1.out.id - o2.out.id;
	}
}
