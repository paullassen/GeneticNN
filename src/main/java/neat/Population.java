package neat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * This class contains populations of networks and manipulates/evolves them
 * toward a higher fitness
 */
public class Population {

	/** MultiMap of all Edges indexed by output node */
	Multimap<Node, Edge> edgeMap = HashMultimap.create();
	
	/** Map of all species atchetypes indexed by species id */
	Map<Integer, Network> archetypeMap = new HashMap<Integer, Network>();

	/** Species map. */
	Multimap<Integer, Network> speciesMap;

	/** List of input nodes */
	List<Node> inList = new ArrayList<Node>();

	/** List of output nodes */
	List<Node> outList = new ArrayList<Node>();

	/** The network list. */
	List<Network> networkList = new ArrayList<Network>();

	/** List of all Networks by Generation */
	List<Network> generation = new ArrayList<Network>();

	FitnessFunction fitFunc;

	Long lastTime = System.currentTimeMillis();

	/** Random Variable */
	Random rand;

	/** The seed. */
	long seed;

	int popSize = 0;

	int topSize = 0;

	/** The base network */
	Network base;

	/** The bias node */
	Node bias;

	/**
	 * Instantiates a new Population to maximize f
	 *
	 * @param f
	 *            the fitness function
	 */
	public Population(FitnessFunction f) {
		seed = System.currentTimeMillis();
		rand = new Random(seed);
		fitFunc = f;
		bias = new Node(0);
		base = new Network(this);
		for (int i = 0; i < f.numInputs(); ++i) {
			Node nod = new Node(0);
			inList.add(nod);
		}
		for (int i = 0; i < f.numOutputs(); ++i) {
			Node nod = new Node(1);
			outList.add(nod);

		}
		for (int i = 0; i < outList.size(); ++i) {
			Node nod = outList.get(i);
			for (int j = -1; j < inList.size(); ++j) {
				base.addEdge((j < 0 ? bias : inList.get(j)), nod);
			}
		}

	}

	/**
	 * Begin evolving the network.
	 *
	 * @param popSize
	 *            the population size
	 * @param maxLoops
	 *            the max number of loops
	 * @return the winning network, null if maxLoops
	 */
	public Network run(int popSize, int maxLoops) {
		double threshold = fitFunc.getThreshold();
		createBasePop(popSize);
		popFitness();
		List<Network> tf = new ArrayList<Network>(generation);
		Network fittest = Collections.min(tf);
		for(int i = 0; i < maxLoops; ++i){
			populateGeneration();
			popFitness();
			tf = new ArrayList<Network>(generation);
			fittest = Collections.min(tf);
			System.out.println("Generation: " + i);
			if(fittest.fitness > threshold){
				System.out.println("Winning Fitness:  " + fittest.fitness);
				return fittest;
			}
		}
		return null;
	}

	/**
	 * Sets the seed for the Random.
	 *
	 * @param seed
	 *            the new seed
	 */
	public void setSeed(long seed) {
		rand.setSeed(seed);
		this.seed = seed;
	}

	/**
	 * Gets the seed.
	 *
	 * @return the seed
	 */
	public long getSeed() {
		return seed;
	}

	/**
	 * Creates the base population.
	 *
	 * @param pSize
	 *            the population size for all generations
	 */
	public void createBasePop(int pSize) {
		popSize = pSize;
		topSize = popSize / 10;

		List<Network> tmpPop = new ArrayList<Network>();
		for (int i = 0; i < popSize; ++i) {
			Network g = new Network(base);
			if (i > 0) {
				for (int j = 0; j < 10; j++) {
					g.mutate();
				}
			}
			networkList.add(g);
			tmpPop.add(g);
		}
		generation = tmpPop;
		speciate();
	}

	/**
	 * Calculates the fitness for each member of the population. Then finds the
	 * most fit Networks and puts them in the topFit and sharedTopFit Sets
	 */
	public void popFitness() {
		for (Network n : generation) {
			n.calculateFitness();
		}
		Network n = Collections.min(generation);
		System.out.printf("Top Fitness:\t%.6f\n", n.fitness);
		System.out.println("_______________________________");
	}

	/**
	 * Checks if two networks are of the same species.
	 *
	 * @param n1
	 *            
	 * @param n2
	 *            
	 * @return true, if the networks share species
	 */
	public boolean compatable(Network n1, Network n2) {
		double distThresh = 1f;
		double c1 = 0.6f; // Weights the excess (e)
		double c2 = 0.6f; // Weights the disjoint (d)
		double c3 = 0.6f; // Weights the weight difference (w)
		Set<Edge> s1 = new TreeSet<Edge>(n1.network.values());
		Set<Edge> s2 = new TreeSet<Edge>(n2.network.values());
		int e = 0;
		int d = 0;
		double w = 0f;
		int N = (s1.size() > s2.size() ? s1.size() : s2.size());
		Edge max1 = Collections.max(n1.network.values());
		Edge max2 = Collections.max(n2.network.values());

		if (max1 != max2) {
			boolean b = max1.getId() > max2.getId();
			int excess = (b ? max2 : max1).getId();
			for (Edge g : (b ? s1 : s2)) {
				if (g.id > excess) {
					e += 1;
				}
			}
		}

		Set<Edge> s = new TreeSet<Edge>(s1);
		s.retainAll(s2);

		d = s1.size() + s2.size() - 2 * s.size() - e;

		for (Edge edg : s) {
			w += Math.abs(edg.getWeight(n1) - edg.getWeight(n2));
		}
		w /= s.size();

		double distance = (c1 * e + c2 * d) / N + c3 * w;

		return distance < distThresh;
	}

	/**
	 * Populate the next generation
	 */
	public void populateGeneration() {

		Map<Integer, Double> sumMap = new TreeMap<Integer, Double>();
		double totalSum = 0f;

		for (int i : speciesMap.keySet()) {
			double sum = 0f;
			int c = 0;
			for (Network n : speciesMap.get(i)) {
				c++;
				sum += n.fitness;
			}
			sum /= c;
			sumMap.put(i, sum);
			totalSum += sum;
		}

		List<List<Network>> spcsList = new ArrayList<List<Network>>();
		List<Integer> topList = new ArrayList<Integer>();
		
		for (int i : speciesMap.keySet()) {
			List<Network> spcs = new ArrayList<Network>(speciesMap.get(i));
			Collections.sort(spcs);
			int top = (int) Math.round((popSize - topSize) * (sumMap.get(i) / totalSum));
			spcsList.add(spcs.subList(0, spcs.size() / 2));
			topList.add(top);
		}

		Set<Network> tmpPop = new TreeSet<Network>();
		for (List<Network> spcs : spcsList) {
			if (!spcs.isEmpty()) {				
				tmpPop.add(spcs.get(0));
			}
		}
		List<Network> tmp = new ArrayList<Network>();
		for (Network n : tmpPop) {
			Network net = new Network(n);
			net.mutate();
			tmp.add(net);
		}
		tmpPop.addAll(tmp);
		for (int i = 0; i < topList.size(); ++i) {
			for (int j = 0; j < topList.get(i); ++j) {
				if (spcsList.get(i).size() == 0 || tmpPop.size() >= popSize) {
					break;
				} else if (spcsList.get(i).size() == 1) {
					if (j * 2 < topList.get(i)) {
						Network net = spcsList.get(i).get(0).mate(spcsList.get(i).get(0));
						net.mutate();
						tmpPop.add(net);
					} else {
						int spc = rand.nextInt(spcsList.size());
						while (spcsList.get(spc).size() == 0) {
							spc = rand.nextInt(spcsList.size());

						}
						int spInd = rand.nextInt(spcsList.get(spc).size());
						Network net = spcsList.get(i).get(0).mate(spcsList.get(spc).get(spInd));
						tmpPop.add(net);
					}
				} else {
					int ind = rand.nextInt(spcsList.get(i).size());
					int spc = rand.nextInt(spcsList.size());
					if (j * 2 < topList.get(i)) {
						spc = i;
					}
					while (spcsList.get(spc).size() == 0) {
						spc = rand.nextInt(spcsList.size());

					}
					int spInd = rand.nextInt(spcsList.get(spc).size());

					Network net = spcsList.get(i).get(ind).mate(spcsList.get(spc).get(spInd));
					tmpPop.add(net);
				}
			}
		}
		tmp = new ArrayList<Network>(tmpPop);
		while (tmpPop.size() < popSize) {
			int r1 = rand.nextInt(tmp.size());
			int r2 = rand.nextInt(tmp.size());

			Network net = tmp.get(r1).mate(tmp.get(r2));
			net.mutate();
			tmpPop.add(net);
		}
		generation = new ArrayList<Network>(tmpPop);
		speciate();

	}

	/**
	 * Define the different species.
	 */
	public void speciate() {
		Multimap<Integer, Network> thisGeneration = HashMultimap.create();
		if (speciesMap == null) {
			archetypeMap.put(0, generation.get(0));
			thisGeneration.put(0, generation.get(0));
		}
		for (Network net : generation) {
			if (!thisGeneration.containsValue(net)) {
				for (int i : archetypeMap.keySet()) {
					if (compatable(net, archetypeMap.get(i))) {
						thisGeneration.put(i, net);
						break;
					}
				}
			}
			if (!thisGeneration.containsValue(net)) {
				int spNum = Collections.max(archetypeMap.keySet()) + 1;
				archetypeMap.put(spNum, net);
				thisGeneration.put(spNum, net);
			}
		}
		speciesMap = thisGeneration;

		/*System.out.println("\nNodeNum: " + Node.count + "\tSpecies: " + thisGeneration.keySet().size() + "\tPopulation: "
				+ generation.size() + "\t Time Since Last: "
				+ (System.currentTimeMillis() - lastTime) / 1000f);
		lastTime = System.currentTimeMillis();*/
	}

	/**
	 * Adds the edge e to this Network.
	 *
	 * @param e
	 *            the edge to add
	 */
	public void addEdge(Edge e) {
		edgeMap.put(e.out, e);
	}
}
