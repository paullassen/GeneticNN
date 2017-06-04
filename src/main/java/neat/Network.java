package neat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.HashMultimap;

/**
 * This class contains the network of Nodes (vertices) and Edges (edges)
 */
class Network implements Comparable<Network>, Runnable {

	static Population pop;

	/** Map containing all edges in Network indexed by input Node */
	HashMultimap<Node, Edge> inNet = HashMultimap.create();

	/** Map containing all edges in Network indexed by output Node */
	HashMultimap<Node, Edge> network = HashMultimap.create();

	double fitness;

	int id;

	private static int count = 0;

	/**
	 * Instantiates a new network. Sets the Population for all later Networks.
	 * Use this Contructor for the base Network
	 *
	 * @param p
	 *            the Population
	 */
	public Network(Population p) {
		pop = p;
		id = count++;
	}

	/**
	 * Instantiates a new network. Copies the Edges from another Network
	 *
	 * @param n
	 *            the Network
	 */
	public Network(Network n) {
		id = count++;
		for (Edge edg : n.network.values()) {
			edg.copyToNetwork(n, this);
			network.put(edg.out, edg);
			inNet.put(edg.in, edg);
		}
	}

	/**
	 * Instantiates a new network. Creates a Network from 2 parents
	 *
	 * @param g1
	 *            Parent 1
	 * @param g2
	 *            Parent 2
	 */
	public Network(Network g1, Network g2) {
		id = count++;
		Network max = (g1.network.values().size() > g2.network.values().size() ? g1 : g2);
		Network min = (g1.network.values().size() > g2.network.values().size() ? g2 : g1);
		for (Edge edg : max.network.values()) {
			edg.copyToNetwork(max, this);
			network.put(edg.out, edg);
			inNet.put(edg.in, edg);
		}
		for (Edge edg : min.network.values()) {
			if (!network.containsValue(edg)) {
				if (loopCheck(edg.in, edg.out)) {
					edg.copyToNetwork(min, this);
					network.put(edg.out, edg);
					inNet.put(edg.in, edg);
				}
			} else {
				Random rand = pop.rand;
				if (rand.nextBoolean()) {
					edg.copyToNetwork(min, this);
				}
			}
		}
	}

	/**
	 * Mutate. Randomly changes the network by adding a node, adding a
	 * connection and changing weights
	 */
	public void mutate() {
		Random rand = pop.rand;
		int r = rand.nextInt(1000);
		if (r < 5) {
			addNode();
		} else if (r < 400) {
			if (!addConnection()) {
				changeWeight();
			}
		} else if (r < 950) {
			changeWeight();

		} else {
			changeAllWeights();
		}
	}

	/**
	 * Adds a node to the network. Each new Node disables a Edge
	 */
	public void addNode() {
		// Inserts a Node in the middle of a Edge

		// Node cannot be inserted into a disabled edge or a bias edge
		List<Edge> edgeList = new ArrayList<Edge>();
		for (Edge e : network.values()) {
			if (e.in == pop.bias && e.getState(this)) {
				// Create a list of viable edges to be disabled
				edgeList.add(e);
			}
		}

		// Choose a edge for the list at random
		Random rand = pop.rand;
		int r = rand.nextInt(edgeList.size());
		Edge edg = edgeList.get(r);

		// If this is the first time the edge is being disabled, create new node
		// Otehrwise use the disabler node
		if (edg.disabler == null) {
			Node n = new Node(1);
			edg.disable(this, n);

			// Each hidden node is connected to 3 nodes (bias, input : output)
			this.addEdge(pop.bias, n);
			this.addEdge(edg.in, n);
			this.addEdge(n, edg.out);
		} else {
			Node n = edg.disabler;
			edg.disable(this);

			// Each hidden node is connected to 3 nodes (bias, input : output)
			this.addEdge(pop.bias, n);
			this.addEdge(edg.in, n);
			this.addEdge(n, edg.out);
		}

	}

	/**
	 * Change all weights.
	 */
	public void changeAllWeights() {
		for (Edge e : network.values()) {
			e.changeWeight(this);
		}
	}

	/**
	 * Change a random weight.
	 */
	public void changeWeight() {
		List<Edge> edgeList = new ArrayList<Edge>(network.values());
		Random rand = pop.rand;
		int r = rand.nextInt(edgeList.size());
		while (!edgeList.get(r).getState(this)) {
			r = rand.nextInt(edgeList.size());
		}
		edgeList.get(r).changeWeight(this);
	}

	/**
	 * Adds the edge between in and out. Create the Edge if it doesn't exist;
	 * Add it from the Population if isn't in the Network Enable it if the Network
	 * contains the disbaled Network
	 *
	 * @param in
	 *            the input Node
	 * @param out
	 *            the output Node
	 */
	public void addEdge(Node in, Node out) {
		if (pop.edgeMap.values().size() > 0) {
			if (!network.values().isEmpty()) {
				for (Edge e : network.get(out)) {
					if (e.in == in) {
						if (e.getState(this)) {
							return;
						} else {
							e.enable(this);
							return;
						}
					}
				}
			}
			for (Edge e : pop.edgeMap.get(out)) {
				if (e.in == in) {
					e.addNetwork(this);
					network.put(out, e);
					inNet.put(in, e);
					return;
				}
			}
		}
		Edge edg = new Edge(in, out, pop.rand);
		pop.addEdge(edg);
		edg.addNetwork(this);
		network.put(out, edg);
		inNet.put(in, edg);
	}

	/**
	 * Adds the connection between two random valid nodes *
	 * 
	 * @return true, if successful
	 */
	public boolean addConnection() {
		List<Node> inList = new ArrayList<Node>();
		List<Node> outList = new ArrayList<Node>();
		Set<Node> inSet = new TreeSet<Node>(inNet.keySet());
		Set<Node> outSet = new TreeSet<Node>(network.keySet());

		for (Node in : inSet) {
			for (Node out : outSet) {
				Set<Edge> s = new HashSet<Edge>(inNet.get(in));
				s.retainAll(network.get(out));
				if (s.isEmpty() && loopCheck(in, out)) {
					inList.add(in);
					outList.add(out);
				} else if (s.size() > 1) {
					throw new IllegalStateException();
				} else {
					for (Edge e : s) {
						if (!e.getState(this) && loopCheck(in, out)) {
							inList.add(in);
							outList.add(out);
						}
					}
				}
			}
		}

		if (inList.isEmpty()) {
			// System.out.println("No possible connections");
			return false;
		} else {
			Random rand = pop.rand;
			int r = rand.nextInt(inList.size());
			addEdge(inList.get(r), outList.get(r));
			return true;
		}

	}

	/**
	 * This function checks whether the proposed connection loops In other
	 * words, checks if the output would affect the input
	 * 
	 * @param in
	 *            the in
	 * @param out
	 *            the out
	 * @return true, if successful
	 */
	public boolean loopCheck(Node in, Node out) {

		if (in == out) {
			return false;
		}
		if (network.get(in).contains(out)) {
			return false;
		}
		if (in.input) {
			return true;
		}
		boolean b = true;
		for (Edge e : network.get(in)) {
			b = (b ? loopCheck(e.in, out) : false);
		}
		return b;
	}

	/**
	 * Prints the network.
	 */
	public void printNetwork() {
		Set<Node> out = new TreeSet<Node>(network.keySet());
		Set<Node> in = new TreeSet<Node>(inNet.keySet());
		Set<Node> all = new TreeSet<Node>(in);
		System.out.println();
		all.addAll(out);
		for (Node o : out) {
			System.out.print("\t" + o.id);
		}
		System.out.println("\n");
		EdgeOutComparator goc = new EdgeOutComparator();
		for (Node i : in) {
			List<Edge> genList = new ArrayList<Edge>(inNet.get(i));
			Collections.sort(genList, goc);
			System.out.print(i.id + "\t");
			List<Node> nodList = new ArrayList<Node>();
			for (Edge g : genList) {
				if (g.getState(this)) {
					nodList.add(g.out);
				}
			}
			for (Node o : out) {
				System.out.print((nodList.contains(o) ? 1 : 0) + "\t");
			}
			System.out.println();
		}
	}

	/**
	 * Prints the Network weights.
	 */
	public void printDouble() {
		Set<Node> out = new TreeSet<Node>(network.keySet());
		Set<Node> in = new TreeSet<Node>(inNet.keySet());
		Set<Node> all = new TreeSet<Node>(in);
		System.out.println();
		all.addAll(out);
		for (Node o : out) {
			System.out.print("\t" + o.id);
		}
		System.out.println("\n");
		new EdgeOutComparator();
		for (Node i : in) {
			new ArrayList<Edge>(inNet.get(i));
			System.out.print(i.id + "\t");
			for (Node o : out) {
				Set<Edge> s = new HashSet<Edge>(inNet.get(i));
				s.retainAll(network.get(o));
				if (!s.isEmpty()) {
					for (Edge g : s) {
						if (g.getState(this)) {
							System.out.printf("%+.2f\t", g.weightMap.get(this));
						} else {
							System.out.print("+O.OO\t");
						}
					}
				} else {
					System.out.printf("%+.2f\t", 0f);
				}

			}
			System.out.println();
		}
	}

	public Edge getEdge(Node in, Node out) {
		// Returns the edge connecting the two nodes if it exists
		// Otehrwise returns null
		Set<Edge> s = new HashSet<Edge>(inNet.get(in));
		s.retainAll(network.get(out));
		for (Edge e : s) {
			return e;
		}
		return null;
	}

	/**
	 * Returns a Network that is the 'Child' of the this Network and the input
	 * Networks
	 *
	 * @param net
	 *            parent
	 * @return the child
	 */
	public Network mate(Network net) {
		if (net == this) {
			Network network = new Network(net);
			// network.mutate();
			return network;
		}
		return new Network(net, this);
	}

	/**
	 * Calculate the output of the Network.
	 *
	 * @param inList
	 *            the list of inputs
	 * @return the list of outputs
	 */
	public List<Double> calculate(List<Double> inList) {
		Map<Node, Double> interimMap = new HashMap<Node,Double>(); 
		if (inList.size() != pop.inList.size()) {
			throw new IllegalArgumentException();
		}
		// Set inputs
		interimMap.put(pop.bias, 1d);
		for (int i = 0; i < pop.inList.size(); ++i) {
			interimMap.put(pop.inList.get(i), inList.get(i));
		}

		List<Double> retList = new ArrayList<Double>();
		for (Node n : pop.outList) {
			retList.add(n.calculateNode(this, interimMap));
		}
		return retList;
	}

	/**
	 * Calculate fitness of this Network.
	 */
	public void calculateFitness() {
		fitness = pop.fitFunc.calculateFitness(this);
	}

	@Override
	public int compareTo(Network arg0) {
		// Compare Networks by shared fitness descending;
		// Then id ascending;
		Double af = arg0.fitness;
		Double tf = this.fitness;
		Integer aid = arg0.id;
		Integer iid = this.id;
		return (af.compareTo(tf) == 0 ? (iid.compareTo(aid)) : af.compareTo(tf));
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		calculateFitness();
	}
}
