package org.neat.neat;

import java.util.*;

import org.neat.activationFunction.*;
import org.neat.aggregationFunction.*;
import org.neat.calculate.*;
import org.neat.gene.*;
import org.neat.genome.*;
import org.neat.libraries.*;
import org.neat.mathematics.Statistics;
import org.neat.species.Species;
import org.neat.species.SpeciesList;

public class Neat {
	
	public final long maxNodes;
	
	public Configuration config = new Configuration();
	
	public Function<Object[], Float> speciesFitnessFunction = new Function<Object[], Float>() {
		@Override
		public Float apply(Object[] t) {
			float max = 0;
			for (Object object : t) {
				max = Math.max(((Genome)object).fitness, max);
			}
			return max;
		}
		@Override
		public String name() {
			return "Maximum";
		};
	};
	
	public RepNodeList nodes = new RepNodeList();
	public RepConnectionList connections = new RepConnectionList(this);
	
	public ActivationFunction defaultActivationFunction = new ActivationSigmoid();
	public AggregationFunction defaultAggregationFunction = new AggregationSum();
	
	public Set<ActivationFunction> activationFunctions = new HashSet<>();
	public Set<AggregationFunction> aggregationFunctions = new HashSet<>();
	
	public SpeciesList speciesList = new SpeciesList();
	public SpeciesList deprecatedSpecies = new SpeciesList();
	
	public GenomeList genomes = new GenomeList();
	public GenomeList savedGenomes = new GenomeList();
	public GenomeList deprecatedGenomes = new GenomeList();
	
	public final int inputCount;
	public final int outputCount;
	public final boolean bias;
	private RepresentativeBiasGene biasNode;
	private boolean initialized = false;
	
	public Neat(int inputCount, int outputCount, boolean addBias) {
		this.inputCount = inputCount;
		this.outputCount = outputCount;
		this.bias = addBias;
		maxNodes = (long) Math.pow(2, 20);
	}
	public Neat(int inputCount, int outputCount, boolean addBias, int maxNodes) {
		this.inputCount = inputCount;
		this.outputCount = outputCount;
		this.bias = addBias;
		this.maxNodes = maxNodes;
	}
	/*public Neat(int inputCount, int outputCount, boolean addBias, ActivationFunction defaultActivationFunction) {
		this.inputCount = inputCount;
		this.outputCount = outputCount;
		this.defaultActivationFunction = defaultActivationFunction;
		this.bias = addBias;
	}
	public Neat(int inputCount, int outputCount, boolean addBias, AggregationFunction defaultAggregationFunction) {
		this.inputCount = inputCount;
		this.outputCount = outputCount;
		this.defaultAggregationFunction = defaultAggregationFunction;
		this.bias = addBias;
	}
	public Neat(int inputCount, int outputCount, boolean addBias, ActivationFunction defaultActivationFunction, AggregationFunction defaultAggregationFunction) {
		this.inputCount = inputCount;
		this.outputCount = outputCount;
		this.defaultActivationFunction = defaultActivationFunction;
		this.defaultAggregationFunction = defaultAggregationFunction;
		this.bias = addBias;
	}*/
	
	public void initialize() {
		initialized = true;
		for (int k = 0; k < inputCount; k++) {
			createNode(0);
		}
		for (int k = 0; k < outputCount; k++) {
			createNode(1);
		}
		if (bias) {
			createBias();
		}
		activationFunctions.add(defaultActivationFunction);
		aggregationFunctions.add(defaultAggregationFunction);
	}
	
	public Genome createGenome() {
		Genome genome = new Genome(this, inputCount, outputCount, genomes.nextId);
		genomes.add(genome);
		genome.initialize();
		return genome;
	}
	public void createGenomes(int count) {
		for (int k = 0; k < count; k++) {
			Genome genome = new Genome(this, inputCount, outputCount, genomes.nextId);
			genome.initialize();
			genomes.add(genome);
		}
	}
	
	public void saveGenomes() {
		for (Entry<Integer, Genome> genome : genomes) {
			savedGenomes.add(genome.getValue());
		}
	}
	public void saveGenomesCopyNew() {
		for (Entry<Integer, Genome> genome : genomes) {
			savedGenomes.add(genome.getValue().copyNew());
		}
	}
	public void saveGenomesCopy() {
		for (Entry<Integer, Genome> genome : genomes) {
			savedGenomes.add(genome.getValue().copy());
		}
	}
	
	public void loadGenomes() {
		for (Entry<Integer, Genome> genome : savedGenomes) {
			genomes.add(genome.getValue());
		}
	}
	public void loadGenomesCopyNew() {
		for (Entry<Integer, Genome> genome : savedGenomes) {
			genomes.add(genome.getValue().copyNew());
		}
	}
	public void loadGenomesCopy() {
		for (Entry<Integer, Genome> genome : savedGenomes) {
			genomes.add(genome.getValue().copy());
		}
	}
	public void clearSavedGenomes() {
		savedGenomes.clear();
	}
	
	public void deleteWorstGenomes(int Count) {
		sortGenomes();
		for (int k = 0; k < Count; k++) {
			genomes.remove(genomes.getList().get(genomes.size()-Count));
		}
	}
	public void cutToBestGenomes(int Count) {
		sortGenomes();
		int size = genomes.size();
		for (int k = 0; k < (size-Count); k++) {
			genomes.remove(genomes.getList().get(Count));
		}
	}
	
	public void sortGenomes() {
		genomes.getList().sort(new Comparator<Genome>() {
			@Override
			public int compare(Genome arg0, Genome arg1) {
				return Float.compare(arg1.fitness, arg0.fitness);
			}
		});
	}
	
	public void cutGenomesInSpecies(float percentage) {
		for (Entry<Integer,Species> species : speciesList) {
			List<Genome> cutGenomes = species.getValue().cutGenomes(percentage);
			genomes.remove(cutGenomes);
			//deprecatedGenomes.add(cutGenomes);
		}
	}
	
	public void clearGenomesOfSpecies() {
		for (Entry<Integer, Species> species : speciesList) {
			species.getValue().genomes.clear();
		}
	}
	
	public void calcSpeciesFitness() {
		for (Entry<Integer, Species> species : speciesList) {
			species.getValue().calcFitness();
		}
	}
	
	public void addSpeciesFitnessToHist() {
		for (Entry<Integer, Species> species : speciesList) {
			species.getValue().addFitness();
		}
	}
	
	public void stagnate(float keepBestSpeciesPerc, float keepBestGenomePerc) {
		int count = (int) Math.ceil(keepBestSpeciesPerc*speciesList.size());
		sortSpecies();
		List<Species> remove = new ArrayList<>();
		for (int k = count; k < speciesList.size(); k++) {
			if (speciesList.getList().get(k).stagnate()) {
				remove.add(speciesList.getList().get(k));
			}
		}
		for (Species species : remove) {
			speciesList.remove(species);
			//deprecatedSpecies.add(species);
			species.sortGenomes();
			count = (int) Math.ceil(keepBestGenomePerc*species.genomes.size());
			for (int k = count; k < species.genomes.size(); k++) {
				Genome genome = species.genomes.getList().get(k);
				genomes.remove(genome);
				//deprecatedGenomes.add(genome);
			}
		}
	}
	
	
	
	public void breed(int Count) {
		sortGenomes();
		int size = genomes.size();
		for (int k = 0; k < Count; k++) {
			double rand1 = Math.pow(Math.random(), 2);
			double rand2 = Math.pow(Math.random(), 2);
			int index1 = (int) Math.floor(rand1*size);
			int index2 = (int) Math.floor(rand2*(size-1));
			if (index2>=index1) {
				index2++;
			}
			Genome genome1 = genomes.getList().get(index1);
			Genome genome2 = genomes.getList().get(index2);
			if (genome1.fitness > genome2.fitness) {
				genomes.add(genome1.crossover(genome2));
			}
			else {
				genomes.add(genome2.crossover(genome1));
			}
		}
	}
	
	public void breedrand(int Count) {
		int size = genomes.size();
		for (int k = 0; k < Count; k++) {
			Genome genome1 = genomes.getRandom();
			Genome genome2 = genomes.getRandom();
			genomes.add(genome1.crossover(genome2));
		}
	}
	
	public void breedSpecies(int Count) {
		sortSpecies();
		for (Entry<Integer, Species> species : speciesList) {
			species.getValue().sortGenomes();
		}
		int size = speciesList.size();
		for (int k = 0; k < Count; k++) {
			double rand1 = Math.pow(Math.random(), 2);
			double rand2 = Math.pow(Math.random(), 2);
			int index1 = (int) Math.floor(rand1*size);
			int index2 = (int) Math.floor(rand2*(size-1));
			if (index2>=index1) {
				index2++;
			}
			index1 = Math.min(index1, size-1);
			index2 = Math.min(index2, size-1);
			Species species1 = speciesList.getList().get(index1);
			Species species2 = speciesList.getList().get(index2);
			if (species1.fitness > species2.fitness) {
				genomes.add(species1.crossover(species2));
			}
			else {
				genomes.add(species2.crossover(species1));
			}
		}
	}
	
	public void breedrandSpecies(int Count) {
		for (int k = 0; k < Count; k++) {
			Species species1 = speciesList.getRandom();
			Species species2 = speciesList.getRandom();
			genomes.add(species1.crossoverRand(species2));
		}
	}
	
	public void breedSpeciesUpTo(int Count) {
		breedSpecies(Math.max(Count-genomes.size(), 0));
	}
	
	public void realignSpeciesRepresentative() {
		for (Entry<Integer, Species> species : speciesList) {
			species.getValue().newRepresentative();
		}
	}
	
	public void specicate() {
		for (Entry<Integer, Genome> genome : genomes) {
			int id = speciesList.getRandom().id;
			float d = speciesList.get(id).distance(genome.getValue());
			for (Entry<Integer, Species> species : speciesList) {
				float d0 = species.getValue().distance(genome.getValue());
				if (d0<d) {
					d = d0;
					id = species.getKey();
				}
			}
			if (d<config.species.compatibilityThreshold) {
				speciesList.get(id).add(genome.getValue());
			}
			else {
				Species species0 = new Species(this, speciesList.nextId(), genome.getValue());
				speciesList.add(species0);
				species0.add(genome.getValue());
			}
		}
	}
	
	public void specicateFast(int Count) {
		for (Entry<Integer, Genome> genome : genomes) {
			int id = speciesList.getRandom().id;
			float d = speciesList.get(id).distance(genome.getValue());
			for (int k = 0; k < Count; k++) {
				Species species = speciesList.getRandom();
				float d0 = species.distance(genome.getValue());
				if (d0<d) {
					d = d0;
					id = species.id;
				}
			}
			if (d<config.species.compatibilityThreshold) {
				speciesList.get(id).add(genome.getValue());
			}
			else {
				Species species0 = new Species(this, speciesList.nextId(), genome.getValue());
				speciesList.add(species0);
				species0.add(genome.getValue());
			}
		}
	}
	public void specicateFast(float percentage) {
		for (Entry<Integer, Genome> genome : genomes) {
			int id = speciesList.getRandom().id;
			float d = speciesList.get(id).distance(genome.getValue());
			int Count = (int)((float)speciesList.size()*Gaussian()*percentage);
			Count = Math.min(Count, speciesList.size());
			for (int k = 0; k < Math.max(1, Count); k++) {
				Species species = speciesList.getRandom();
				float d0 = species.distance(genome.getValue());
				if (d0<d) {
					d = d0;
					id = species.id;
				}
			}
			if (d<config.species.compatibilityThreshold) {
				speciesList.get(id).add(genome.getValue());
			}
			else {
				Species species0 = new Species(this, speciesList.nextId(), genome.getValue());
				speciesList.add(species0);
				species0.add(genome.getValue());
			}
		}
	}
	
	public void sortSpecies() {
		for (Entry<Integer,Species> species : speciesList) {
			species.getValue().calcFitness();
		}
		speciesList.getList().sort(new Comparator<Species>() {
			@Override
			public int compare(Species o1, Species o2) {
				return Float.compare(o1.fitness, o2.fitness);
			}
		}.reversed());
	}
	public void mutate() {
		for (Genome genome : genomes.getList()) {
			genome.mutate();
		}
	}
	public void mutate(float factor) {
		for (Genome genome : genomes.getList()) {
			genome.mutate(factor);
		}
	}
	
	public List<Calculator> createCalculators() {
		List<Calculator> calculators = new ArrayList();
		calculators.clear();
		for (int k = 0; k < genomes.size(); k++) {
			calculators.add(new Calculator(genomes.getList().get(k)));
			calculators.get(k).initialize();
		}
		return calculators;
	}
	
	
	public void initSpecicate() {
		float d = -1;
		Genome center = genomes.getRandom();
		for (Entry<Integer,Genome> genome1 : genomes) {
			float d0 = 0;
			for (Entry<Integer, Genome> genome2 : genomes) {
				d0 += genome1.getValue().distance(genome2.getValue());
			}
			if (d<0) {
				d = d0;
				center = genome1.getValue();
			}
			if (d0<d) {
				center = genome1.getValue();
				d = d0;
			}
		}
		Species species0 = new Species(this, speciesList.nextId(), center);
		speciesList.add(species0);
		/*for (Entry<Integer, Genome> genome : genomes) {
			int id = speciesList.getRandom().id;
			d = speciesList.get(id).distance(genome.getValue());
			for (Entry<Integer, Species> species : speciesList) {
				float d0 = species.getValue().distance(genome.getValue());
				if (d0<d) {
					d = d0;
					id = species.getKey();
				}
			}
			if (d<config.species.compatibilityThreshold) {
				speciesList.get(id).add(genome.getValue());
			}
			else {
				species0 = new Species(this, speciesList.nextId(), genome.getValue());
				speciesList.add(species0);
				species0.add(genome.getValue());
			}
		}*/
		//specicate();
	}
	public void initSpecicateFast(float percentage) {
		float d = -1;
		Genome center = genomes.getRandom();
		int Count = (int)((float)genomes.size()*Gaussian()*percentage);
		Count = Math.min(Count, genomes.size());
		for (int k1 = 0; k1 < Count; k1++) {
			Genome genome1 = genomes.getRandom();
			int Count2 = (int)((float)genomes.size()*Gaussian()*percentage);
			Count2 = Math.min(Count, genomes.size());
			float d0 = 0;
			for (int k2 = 0; k2 < Count2; k2++) {
				Genome genome2 = genomes.getRandom();
				d0 += genome1.distance(genome2);
			}
			if (d<0) {
				d = d0;
				center = genome1;
			}
			if (d0<d) {
				center = genome1;
				d = d0;
			}
		}
		Species species0 = new Species(this, speciesList.nextId(), center);
		speciesList.add(species0);
		/*for (Entry<Integer, Genome> genome : genomes) {
			int id = speciesList.getRandom().id;
			d = speciesList.get(id).distance(genome.getValue());
			for (Entry<Integer, Species> species : speciesList) {
				float d0 = species.getValue().distance(genome.getValue());
				if (d0<d) {
					d = d0;
					id = species.getKey();
				}
			}
			if (d<config.species.compatibilityThreshold) {
				speciesList.get(id).add(genome.getValue());
			}
			else {
				species0 = new Species(this, speciesList.nextId(), genome.getValue());
				speciesList.add(species0);
				species0.add(genome.getValue());
			}
		}*/
		//specicate();
	}
	public void initSpecicateRand() {
		speciesList.add(new Species(this, 0, genomes.getRandom()));
	}
	
	public void removeEmptySpecies() {
		List<Species> remove = new ArrayList<>();
		for (Entry<Integer, Species> species : speciesList) {
			if (species.getValue().genomes.size()==0) {
				remove.add(species.getValue());
			}
		}
		for (Species species : remove) {
			speciesList.remove(species);
			deprecatedSpecies.add(species);
		}
	}
	/*public int getNeededSpeciesCount() {
		Statistics statistics = new Statistics();
		for (Entry<Integer, Genome> genome1 : genomes) {
			for (Entry<Integer, Genome> genome2 : genomes) {
				if (!genome1.equals(genome2)) {
					statistics.add(genome1.getValue().distance(genome2.getValue()));
				}
			}
		}
		return (int) Math.ceil(statistics.variance()/this.config.species.compatibilityThreshold);
	}
	*/
	
	
	public NodeGene addNode(float x, Genome genome) {
		if (nodes.getNextInnovationNumber()==maxNodes) {
			throw new IllegalStateException("Amount of Nodes has exceeded the Maximum!");
		}
		RepresentativeNodeGene repGene = new RepresentativeNodeGene(this, nodes.getNextInnovationNumber(), x);
		nodes.add(repGene);
		NodeGene gene = repGene.createGene(genome);
		gene.activation = genome.activation;
		gene.aggregation = genome.aggregation;
		return gene;
	}
	public void createNode(float x) {
		RepresentativeNodeGene gene = new RepresentativeNodeGene(this, nodes.getNextInnovationNumber(), x);
		nodes.add(gene);
	}
	public void createBias() {
		RepresentativeBiasGene gene = new RepresentativeBiasGene(this, nodes.getNextInnovationNumber(), 0);
		nodes.add(gene);
		biasNode = gene;
	}
	public NodeGene getNode(long innovationNumber, Genome genome) {
		if (!nodes.contains(innovationNumber)) {
			throw new IllegalArgumentException("Innovation Number does not exist!");
		}
		NodeGene gene = nodes.get(innovationNumber).createGene(genome);
		gene.activation = genome.activation;
		gene.aggregation = genome.aggregation;
		return gene;
	}
	public NodeGene getNode(NodeGene node, Genome genome) {
		if (!nodes.contains(node)) {
			throw new IllegalArgumentException("Innovation Number does not exist!");
		}
		NodeGene gene = nodes.get(node).createGene(genome);
		gene.activation = genome.activation;
		gene.aggregation = genome.aggregation;
		return gene;
	}
	public RepresentativeNodeGene getRepresentativeNode(long innovationNumber) {
		if (!nodes.contains(innovationNumber)) {
			throw new IllegalArgumentException("Innovation Number does not exist!");
		}
		return nodes.get(innovationNumber);
	}
	public BiasGene getBias(Genome genome) {
		if (!bias) {
			throw new IllegalArgumentException("Bias not activated");
		}
		BiasGene gene = biasNode.createGene(genome);
		gene.activation = defaultActivationFunction;
		gene.aggregation = defaultAggregationFunction;
		return gene;
	}
	
	public ConnectionGene getConnection(Genome genome, NodeGene from, NodeGene to) {
		if (from.x >= to.x) {
			NodeGene temp = from;
			from = to;
			to = temp;
		}
		if (connections.contains(from, to)) {
			return connections.get(from, to).createGene(genome, from, to);
		}
		else {
			RepresentativeConnectionGene gene = new RepresentativeConnectionGene(this, from.getRepresentative(), to.getRepresentative());
			connections.add(gene);
			return gene.createGene(genome, from, to);
		}
	}
	public RepresentativeConnectionGene getRepresentativeConnection(RepresentativeNodeGene from, RepresentativeNodeGene to) {
		if (from.x >= to.x) {
			RepresentativeNodeGene temp = from;
			from = to;
			to = temp;
		}
		if (connections.contains(from, to)) {
			return connections.get(from, to);
		}
		else {
			RepresentativeConnectionGene gene = new RepresentativeConnectionGene(this, from, to);
			connections.add(gene);
			return gene;
		}
	}
	
	public boolean hasNode(int innovationNumber) {
		return nodes.contains(innovationNumber);
	}
	public boolean hasConnection(int innovationNumber) {
		return connections.contains(innovationNumber);
	}
	
	public boolean isInitialized() {
		return initialized;
	}
	public RepresentativeConnectionGene getRepresentativeConnection(NodeGene from, NodeGene to) {
		return getRepresentativeConnection(from.getRepresentative(), to.getRepresentative());
	}
	public NodeGene getNode(RepresentativeNodeGene node, Genome genome) {
		if (!nodes.contains(node)) {
			throw new IllegalArgumentException("Innovation Number does not exist!");
		}
		NodeGene gene = nodes.get(node).createGene(genome);
		gene.activation = genome.activation;
		gene.aggregation = genome.aggregation;
		return gene;
	}
	
	@Override
	public String toString() {
		String string = "\n";
		if (!initialized) {
			string += "Not yet initialized\n\n";
		}
		string += "Nodes:\n";
		string += nodes;
		string += "\n\nConnections:\n";
		string += connections;
		string += "\n\nGenomes:\n";
		string += genomes;
		string += "\n\n";
		string += activationFunctions.getClass().getSimpleName();
		string += "\n";
		string += aggregationFunctions.getClass().getSimpleName();
		string += "\n";
		return string;
	}
	
	private double nextGaussian() {
		double v1, v2, s;
		do {
			v1 = 2 * Math.random() -1;
			v2 = 2 * Math.random() -1;
			s = Math.pow(v1, 2) + Math.pow(v2, 2);
		} while (s >= 1 || s == 0);
		double multipier = Math.sqrt(-2 * Math.log(s)/s);
		return v1 * multipier;
	}
	private double Gaussian() {
		double rand = nextGaussian();
		return Math.exp(rand);
	}
	
}
