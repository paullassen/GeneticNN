package neat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * This class contains teh netwrok of Nodes (vertices) and Genes (edges)
 */
class Genome implements Comparable<Genome> {

	static OverGen og;

	/** Map containing all genes in Genome indexed by input Node */
	HashMultimap<Node, Gene> genIn = HashMultimap.create();

	/** Map containing all genes in Genome indexed by output Node */
	HashMultimap<Node, Gene> genome = HashMultimap.create();

	float fitness;

	float sharedFitness;

	int id;

	private static int count = 0;

	/**
	 * Instantiates a new genome. Sets the OverGen for all later Genomes Use
	 * this Contructor for the base Genome
	 *
	 * @param o
	 *            the OverGen
	 */
	public Genome(OverGen o) {
		og = o;
		id = count++;
	}

	/**
	 * Instantiates a new genome. Copies the Genes from another Genome
	 *
	 * @param g
	 *            the Genome
	 */
	public Genome(Genome g) {
		id = count++;
		for (Gene gen : g.genome.values()) {
			gen.copyToGenome(g, this);
			genome.put(gen.out, gen);
			genIn.put(gen.in, gen);
		}
	}

	/**
	 * Instantiates a new genome. Creates a Genome from 2 parents
	 *
	 * @param g1
	 *            Parent 1
	 * @param g2
	 *            Parent 2
	 */
	public Genome(Genome g1, Genome g2) {
		id = count++;
		Genome max = (g1.genome.values().size() > g2.genome.values().size() ? g1 : g2);
		Genome min = (g1.genome.values().size() > g2.genome.values().size() ? g2 : g1);
		for (Gene gen : max.genome.values()) {
			gen.copyToGenome(max, this);
			genome.put(gen.out, gen);
			genIn.put(gen.in, gen);
		}
		for (Gene gen : min.genome.values()) {
			if (!genome.containsValue(gen)) {
				if (loopCheck(gen.in, gen.out)) {
					gen.copyToGenome(min, this);
					genome.put(gen.out, gen);
					genIn.put(gen.in, gen);
				}
			} else {
				Random rand = og.rand;
				if (rand.nextBoolean()) {
					gen.copyToGenome(min, this);
				}
			}
		}
	}

	/**
	 * Mutate. Randomly changes the network by adding a node, adding a
	 * connection and changing weights
	 */
	public void mutate() {
		Random rand = og.rand;
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
	 * Adds a node to the network. Each new Node disables a Gene
	 */
	public void addNode() {
		// Inserts a Node in the middle of a Gene

		// Node cannot be inserted into a disabled gene or a bias gene
		List<Gene> genList = new ArrayList<Gene>();
		for (Gene g : genome.values()) {
			if (!g.in.bias && g.getState(this)) {
				// Create a list of viable genes to be disabled
				genList.add(g);
			}
		}

		// Choose a gene for the list at random
		Random rand = og.rand;
		int r = rand.nextInt(genList.size());
		Gene gen = genList.get(r);

		// If this is the first time the gene is being disabled, create new node
		// Otehrwise use the disabler node
		if (gen.disabler == null) {
			Node n = new Node();
			gen.disable(this, n);

			// Each hidden node is connected to 3 nodes (bias, input : output)
			this.addGene(og.bias, n);
			this.addGene(gen.in, n);
			this.addGene(n, gen.out);
		} else {
			Node n = gen.disabler;
			gen.disable(this);

			// Each hidden node is connected to 3 nodes (bias, input : output)
			this.addGene(og.bias, n);
			this.addGene(gen.in, n);
			this.addGene(n, gen.out);
		}

	}

	/**
	 * Change all weights.
	 */
	public void changeAllWeights() {
		for (Gene g : genome.values()) {
			g.changeWeight(this);
		}
	}

	/**
	 * Change a random weight.
	 */
	public void changeWeight() {
		List<Gene> genList = new ArrayList<Gene>(genome.values());
		Random rand = og.rand;
		int r = rand.nextInt(genList.size());
		while (!genList.get(r).getState(this)) {
			r = rand.nextInt(genList.size());
		}
		genList.get(r).changeWeight(this);
	}

	/**
	 * Adds the gene between in and out. Create the Gene if it doesn't exist;
	 * Add it from the OverGen if isn't in the Genome Enable it if the Genome
	 * contains the disbaled Genome
	 *
	 * @param in
	 *            the input Node
	 * @param out
	 *            the output Node
	 */
	public void addGene(Node in, Node out) {
		if (og.geneMap.values().size() > 0) {
			if (!genome.values().isEmpty()) {
				for (Gene g : genome.get(out)) {
					if (g.in == in) {
						if (g.getState(this)) {
							return;
						} else {
							g.enable(this);
							return;
						}
					}
				}
			}
			for (Gene g : og.geneMap.get(out)) {
				if (g.in == in) {
					g.addGenome(this);
					genome.put(out, g);
					genIn.put(in, g);
					return;
				}
			}
		}
		Gene gen = new Gene(in, out, og.rand);
		og.addGene(gen);
		gen.addGenome(this);
		genome.put(out, gen);
		genIn.put(in, gen);
	}

	/**
	 * Adds the connection between two random valid nodes *
	 * 
	 * @return true, if successful
	 */
	public boolean addConnection() {
		List<Node> inList = new ArrayList<Node>();
		List<Node> outList = new ArrayList<Node>();
		Set<Node> inSet = new TreeSet<Node>(genIn.keySet());
		Set<Node> outSet = new TreeSet<Node>(genome.keySet());

		for (Node in : inSet) {
			for (Node out : outSet) {
				Set<Gene> s = new HashSet<Gene>(genIn.get(in));
				s.retainAll(genome.get(out));
				if (s.isEmpty() && loopCheck(in, out)) {
					inList.add(in);
					outList.add(out);
				} else if (s.size() > 1) {
					throw new IllegalStateException();
				} else {
					for (Gene g : s) {
						if (!g.getState(this) && loopCheck(in, out)) {
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
			Random rand = og.rand;
			int r = rand.nextInt(inList.size());
			addGene(inList.get(r), outList.get(r));
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
		if (genome.get(in).contains(out)) {
			return false;
		}
		if (in.bias || in.input) {
			return true;
		}
		boolean b = true;
		for (Gene g : genome.get(in)) {
			b = (b ? loopCheck(g.in, out) : false);
		}
		return b;
	}

	/**
	 * Prints the genome.
	 */
	public void printGenome() {
		Set<Node> out = new TreeSet<Node>(genome.keySet());
		Set<Node> in = new TreeSet<Node>(genIn.keySet());
		Set<Node> all = new TreeSet<Node>(in);
		System.out.println();
		all.addAll(out);
		for (Node o : out) {
			System.out.print("\t" + o.id);
		}
		System.out.println("\n");
		GeneOutComparator goc = new GeneOutComparator();
		for (Node i : in) {
			List<Gene> genList = new ArrayList<Gene>(genIn.get(i));
			Collections.sort(genList, goc);
			System.out.print(i.id + "\t");
			List<Node> nodList = new ArrayList<Node>();
			for (Gene g : genList) {
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
	 * Prints the Genome weights.
	 */
	public void printFloat() {
		Set<Node> out = new TreeSet<Node>(genome.keySet());
		Set<Node> in = new TreeSet<Node>(genIn.keySet());
		Set<Node> all = new TreeSet<Node>(in);
		System.out.println();
		all.addAll(out);
		for (Node o : out) {
			System.out.print("\t" + o.id);
		}
		System.out.println("\n");
		new GeneOutComparator();
		for (Node i : in) {
			new ArrayList<Gene>(genIn.get(i));
			System.out.print(i.id + "\t");
			for (Node o : out) {
				Set<Gene> s = new HashSet<Gene>(genIn.get(i));
				s.retainAll(genome.get(o));
				if (!s.isEmpty()) {
					for (Gene g : s) {
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

	public Gene getGene(Node in, Node out) {
		// Returns the gene connecting the two nodes if it exists
		// Otehrwise returns null
		Set<Gene> s = new HashSet<Gene>(genIn.get(in));
		s.retainAll(genome.get(out));
		for (Gene g : s) {
			return g;
		}
		return null;
	}
	/**
	 * Returns a Genome that is the 'Child' of the this Genome and the input
	 * Genomes
	 *
	 * @param gnm
	 *            parent
	 * @return the child
	 */
	public Genome mate(Genome gnm) {
		if (gnm == this) {
			Genome genome = new Genome(gnm);
			//genome.mutate();
			return genome;
		}
		return new Genome(gnm, this);
	}
	/**
	 * Calculate the output of the Genome.
	 *
	 * @param inList
	 *            the list of inputs
	 * @return the list of outputs
	 */
	public List<Float> calculate(List<Float> inList) {
		if (inList.size() != og.inList.size()) {
			throw new IllegalArgumentException();
		}
		// Set inputs
		for (int i = 0; i < og.inList.size(); ++i) {
			Node n = og.inList.get(i);
			n.value = inList.get(i);
		}

		for (Node n : genome.keySet()) {
			n.value = 0f;
			n.calculated = false;
		}
		List<Float> retList = new ArrayList<Float>();
		for (Node n : og.outList) {
			retList.add(n.calculateNode(this));
		}
		return retList;
	}

	/**
	 * Calculate fitness of this Genome.
	 */
	public void calculateFitness() {
		fitness = og.fitFunc.calculateFitness(this);
		/*
		 * ArrayList<Float> inList = new ArrayList<Float>(); inList.add(1f);
		 * inList.add(1f); float d1 = calculate(inList).get(0); inList.set(0,
		 * 0f); inList.set(1, 0f); float d2 = calculate(inList).get(0);
		 * inList.set(0, 1f); inList.set(1, 0f); float d3 = 1f -
		 * calculate(inList).get(0); inList.set(0, 0f); inList.set(1, 1f); float
		 * d4 = 1f - calculate(inList).get(0); float d = d1 + d2 + d3 + d4;
		 * fitness = (4f - d) * (4f - d);
		 */

	}

	/**
	 * Calculate shared fitness of this Genome.
	 */
	public void calculateSharedFitness() {

		while (og.sfMap.size() <= og.gen) {
			Multimap<Integer, Float> hmm = HashMultimap.create();
			og.sfMap.add(hmm);
		}
		Multimap<Integer, Genome> spm = og.speciesMap.get(og.gen);
		for (int i : spm.keySet()) {
			if (spm.get(i).contains(this)) {
				int spSize = og.speciesMap.get(og.gen).get(i).size();

				sharedFitness = fitness / spSize;
				og.sfMap.get(og.gen).put(i, sharedFitness);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Genome arg0) {
		// Compare Genomes by shared fitness descending;
		// Then id ascending;
		Float af = arg0.sharedFitness;
		Float tf = this.sharedFitness;
		Integer aid = arg0.id;
		Integer iid = this.id;
		return (af.compareTo(tf) == 0 ? (iid.compareTo(aid)) : af.compareTo(tf));
	}
}
