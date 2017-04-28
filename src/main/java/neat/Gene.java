package neat;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

/**
 * Genes function as edges of a network. Genes are static, so all of the Genomes
 * share from the same pool; Genes maintain maps of Genome specific variables,
 * their weights and states (enabled/disabled);
 */
class Gene implements Comparable<Gene> {

	static int count = 0;

	final int id;

	final Node in, out;

	private final Random rand;

	/**
	 * The weight map contains a float for each Genome in which is is contained
	 */
	HashMap<Genome, Float> weightMap = new HashMap<Genome, Float>();

	/**
	 * The state map contains a boolean (representing enabled or disabled) for
	 * each Genome in which it is contained
	 */
	HashMap<Genome, Boolean> stateMap = new HashMap<Genome, Boolean>();

	/** This is the */
	Node disabler = null;

	/**
	 * Instantiates a new gene.
	 *
	 * @param inp
	 *            the input Node
	 * @param outp
	 *            the output Node
	 * @param g
	 *            the OverGen
	 */
	public Gene(Node inp, Node outp, Random rand) {
		id = count++;
		in = inp;
		out = outp;
		this.rand = rand;
	}

	public int getId() {
		return id;
	}

	public float getWeight(Genome g) {
		return weightMap.get(g);
	}

	/**
	 * Change weight for the specified genome by a random amount.
	 *
	 * @param g
	 *            the Genome
	 */
	public void changeWeight(Genome g) {
		float weight = weightMap.get(g).floatValue();
		weight = weight + rand.nextFloat() * 4 - 2f;
		weightMap.replace(g, weight);
	}

	public Node getDisabler() {
		return disabler;
	}

	/**
	 * Adds this gene to a Genome, with the same weight and state of another
	 * Genome.
	 *
	 * @param from
	 *            the Genome from which the Gene is copied
	 * @param to
	 *            the Genome to which the Gene is copied
	 */
	public void copyToGenome(Genome from, Genome to) {
		weightMap.put(to, weightMap.get(from));
		stateMap.put(to, stateMap.get(from));
	}

	/**
	 * Adds this Gene to a Genome, with a random weight;
	 * 
	 * @param g
	 *            the Genome to which the Gene is added
	 */
	public void addGenome(Genome g) {
		float weight = rand.nextFloat() * 4 - 2;
		weightMap.put(g, weight);
		this.enable(g);
	}

	public boolean getState(Genome g) {
		return stateMap.get(g);
	}

	/**
	 * Disable this Gene in the specified Genome. Use this the first time Gene
	 * is disabled. The disabler is set to n.
	 *
	 * @param g
	 *            the Genome
	 * @param n
	 *            the Disabling Node
	 */
	public void disable(Genome g, Node n) {
		stateMap.put(g, false);
		disabler = n;
	}

	/**
	 * Disable this gene for Genome g
	 *
	 * @param g
	 *            the Genome
	 */
	public void disable(Genome g) {
		stateMap.put(g, false);
		// enabled = false;
	}

	/**
	 * Enables this Gene for the Genome
	 *
	 * @param g
	 *            the Genome
	 */
	public void enable(Genome g) {
		stateMap.put(g, true);
	}

	@Override
	public int compareTo(Gene arg0) {
		// Compare by id
		return this.getId() - arg0.getId();
	}
}

class GeneOutComparator implements Comparator<Gene> {
	@Override
	public int compare(Gene o1, Gene o2) {
		return o1.out.id - o2.out.id;
	}
}
