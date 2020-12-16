package org.neat.neat;

import java.util.*;

import org.neat.activationFunction.*;
import org.neat.aggregationFunction.*;
import org.neat.calculate.*;
import org.neat.gene.*;
import org.neat.genome.*;
import org.neat.libraries.*;
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
	
	public ActivationFunction defaultActivationFunction;
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
		defaultActivationFunction = new ActivationSigmoid(this);
		this.inputCount = inputCount;
		this.outputCount = outputCount;
		this.bias = addBias;
		maxNodes = (long) Math.pow(2, 20);
	}
	public Neat(int inputCount, int outputCount, boolean addBias, int maxNodes) {
		defaultActivationFunction = new ActivationSigmoid(this);
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
	public void saveBestGenomes(float percentage) {
		for (int k = 0; k < (float)genomes.size()*percentage; k++) {
			savedGenomes.add(genomes.getList().get(k));
		}
	}
	public void saveBestGenomesCopy(float percentage) {
		for (int k = 0; k < (float)genomes.size()*percentage; k++) {
			savedGenomes.add(genomes.getList().get(k).copy());
		}
	}
	public void saveBestGenomesCopyNew(float percentage) {
		for (int k = 0; k < (float)genomes.size()*percentage; k++) {
			savedGenomes.add(genomes.getList().get(k).copyNew());
		}
	}
	public void removeWorstSaved(float percentage) {
		int count = (int) (percentage*savedGenomes.size());
		for (int k = 0; k < count; k++) {
			savedGenomes.getList().remove(savedGenomes.size()-1);
		}
	}
	
	public void deleteWorstGenomes(int count) {
		sortGenomes();
		for (int k = 0; k < count; k++) {
			genomes.remove(genomes.getList().get(genomes.size()-count));
		}
	}
	public void cutToBestGenomes(int count) {
		sortGenomes();
		int size = genomes.size();
		for (int k = 0; k < (size-count); k++) {
			genomes.remove(genomes.getList().get(count));
		}
	}
	
	public void sortGenomes() {
		genomes.getList().sort(genomes.getComparator().reversed());
	}
	
	public void cutGenomesInSpecies(float percentage) {
		for (Entry<Integer,Species> species : speciesList) {
			List<Genome> cutGenomes = species.getValue().cutGenomes(percentage);
			genomes.remove(cutGenomes);
			//deprecatedGenomes.add(cutGenomes);
		}
	}
	
	public void removeGenomesBelow(float fitness) {
		List<Genome> removeGenomes = new ArrayList<>();
		for (Entry<Integer, Genome> genome : genomes) {
			if (genome.getValue().fitness<fitness) {
				removeGenomes.add(genome.getValue());
			}
		}
		for (Genome genome : removeGenomes) {
			genomes.remove(genome);
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
	
	public void createSpecies() {
		Genome representative = genomes.getRandom().copy();
		Species newSpecies = new Species(this, speciesList.nextId(), representative);
		speciesList.add(newSpecies);
	}
	
	public void createSpecies(int count) {
		for (int k = 0; k < count; k++) {
			Genome representative = genomes.getRandom().copy();
			Species newSpecies = new Species(this, speciesList.nextId(), representative);
			speciesList.add(newSpecies);
		}
	}
	
	public void stagnate(float keepBestSpeciesPerc, float keepBestGenomePerc) {
		int count = (int) Math.ceil(keepBestSpeciesPerc*speciesList.size());
		List<Species> remove = new ArrayList<>();
		for (int k = count; k < speciesList.size(); k++) {
			if (speciesList.getList().get(k).stagnate()) {
				remove.add(speciesList.getList().get(k));
			}
		}
		if (config.species.fixedCount) {
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
				createSpecies();
			}
		}
		else {
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
	}
	
	
	
	public void breed(int count) {
		int size = genomes.size();
		for (int k = 0; k < count; k++) {
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
	
	public void breedrand(int count) {
		int size = genomes.size();
		for (int k = 0; k < count; k++) {
			Genome genome1 = genomes.getRandom();
			Genome genome2 = genomes.getRandom();
			genomes.add(genome1.crossover(genome2));
		}
	}
	
	public void breedSpecies(int count) {
		List<Integer> ids = new ArrayList<>();
		for (Entry<Integer, Species> species : speciesList) {
			species.getValue().sortGenomes();
			if (species.getValue().genomes.size()>0) {
				ids.add(species.getKey());
			}
		}
		int size = ids.size();
		int k = 0;
		while (k < count) {
			double rand1 = Math.pow(Math.random(), 2);
			double rand2 = Math.pow(Math.random(), 2);
			int index1 = (int) Math.floor(rand1*size);
			int index2 = (int) Math.floor(rand2*(size));
			index1 = Math.min(index1, size-1);
			index2 = Math.min(index2, size-1);
			Species species1 = speciesList.get(ids.get(index1));
			Species species2 = speciesList.get(ids.get(index2));
			if (species1.genomes.size()<1 || species2.genomes.size()<1) {
				continue;
			}
			if (species1.fitness > species2.fitness) {
				genomes.add(species1.crossover(species2));
			}
			else {
				genomes.add(species2.crossover(species1));
			}
			k++;
		}
	}
	
	public void breedrandSpecies(int count) {
		int k = 0;
		while (k < count) {
			Species species1 = speciesList.getRandomNotEmpty();
			Species species2 = speciesList.getRandomNotEmpty();
			if (species1.genomes.size()<1||species2.genomes.size()<1) {
				continue;
			}
			genomes.add(species1.crossoverRand(species2));
			k++;
		}
	}
	
	public void breedSpeciesUpTo(int count) {
		breedSpecies(Math.max(count-genomes.size(), 0));
	}
	
	public void realignSpeciesRepresentative() {
		for (Entry<Integer, Species> species : speciesList) {
			if (species.getValue().genomes.size()<1) {
				continue;
			}
			species.getValue().newRepresentative();
		}
	}
	
	public void specicate() {
		if (config.species.fixedCount) {
			for (Entry<Integer, Genome> genome : genomes) {
				int id = speciesList.getRandom().id;
				float d = speciesList.get(id).distance(genome.getValue());
				for (Entry<Integer, Species> species : speciesList) {
					float d0 = species.getValue().distance(genome.getValue());
					if (d0<d) {
						d = d0;
						id = species.getKey();
					}
					if (d<=config.species.acceptAlwaysCompability) {
						break;
					}
				}
				speciesList.get(id).add(genome.getValue());
			}
		}
		else {
			if (config.species.findNearest) {
				for (Entry<Integer, Genome> genome : genomes) {
					if (config.species.shuffleDuringSpecicate) {
						shuffleSpecies();
					}
					int id = speciesList.getRandom().id;
					float d = speciesList.get(id).distance(genome.getValue());
					for (Entry<Integer, Species> species : speciesList) {
						float d0 = species.getValue().distance(genome.getValue());
						if (d0<d) {
							d = d0;
							id = species.getKey();
						}
						if (d<=config.species.acceptAlwaysCompability) {
							break;
						}
					}
					if (d<=config.species.compatibilityThreshold) {
						speciesList.get(id).add(genome.getValue());
					}
					else {
						Species species0 = new Species(this, speciesList.nextId(), genome.getValue());
						speciesList.add(species0);
						species0.add(genome.getValue());
					}
				}
			}
			else {
				for (Entry<Integer, Genome> genome : genomes) {
					if (config.species.shuffleDuringSpecicate) {
						shuffleSpecies();
					}
					boolean specicated = false;
					for (Entry<Integer, Species> species : speciesList) {
						float d = species.getValue().distance(genome.getValue());
						if (d<=config.species.compatibilityThreshold) {
							species.getValue().add(genome.getValue());
							specicated = true;
							break;
						}
					}
					if (specicated) {
						continue;
					}
					Species species = new Species(this, speciesList.nextId(), genome.getValue());
					speciesList.add(species);
					species.add(genome.getValue());
				}
			}
		}
	}
	
	@Deprecated
	public void specicateFast(int count) {
		if (config.species.fixedCount) {
			for (Entry<Integer, Genome> genome : genomes) {
				int id = speciesList.getRandom().id;
				float d = speciesList.get(id).distance(genome.getValue());
				for (int k = 0; k < count; k++) {
					Species species = speciesList.getRandom();
					float d0 = species.distance(genome.getValue());
					if (d0<d) {
						d = d0;
						id = species.id;
					}
					if (d<=config.species.acceptAlwaysCompability) {
						break;
					}
				}
				speciesList.get(id).add(genome.getValue());
			}
		}
		else {
			if (config.species.findNearest) {
				for (Entry<Integer, Genome> genome : genomes) {
					int id = speciesList.getRandom().id;
					float d = speciesList.get(id).distance(genome.getValue());
					for (int k = 0; k < count; k++) {
						Species species = speciesList.getRandom();
						float d0 = species.distance(genome.getValue());
						if (d0<d) {
							d = d0;
							id = species.id;
						}
						if (d<=config.species.acceptAlwaysCompability) {
							break;
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
			else {
				for (Entry<Integer, Genome> genome : genomes) {
					boolean specicated = false;
					for (int k = 0; k < count; k++) {
						Species species = speciesList.getRandom();
						float d = species.distance(genome.getValue());
						if (d<config.species.compatibilityThreshold) {
							species.add(genome.getValue());
							specicated = true;
							break;
						}
					}
					if (specicated) {
						continue;
					}
					else {
						Species species = new Species(this, speciesList.nextId(), genome.getValue());
						speciesList.add(species);
						species.add(genome.getValue());
					}
				}
			}
		}
	}
	@Deprecated
	public void specicateFast(float percentage) {
		if (config.species.fixedCount) {
			for (Entry<Integer, Genome> genome : genomes) {
				int id = speciesList.getRandom().id;
				float d = speciesList.get(id).distance(genome.getValue());
				int count = (int)((float)speciesList.size()*Gaussian()*percentage);
				count = Math.min(count, speciesList.size());
				for (int k = 0; k < count; k++) {
					Species species = speciesList.getRandom();
					float d0 = species.distance(genome.getValue());
					if (d0<d) {
						d = d0;
						id = species.id;
					}
					if (d<=config.species.acceptAlwaysCompability) {
						break;
					}
				}
				speciesList.get(id).add(genome.getValue());
			}
		}
		else {
			if (config.species.findNearest) {
				for (Entry<Integer, Genome> genome : genomes) {
					int id = speciesList.getRandom().id;
					float d = speciesList.get(id).distance(genome.getValue());
					int count = (int)((float)speciesList.size()*Gaussian()*percentage);
					count = Math.min(count, speciesList.size());
					for (int k = 0; k < Math.max(1, count); k++) {
						Species species = speciesList.getRandom();
						float d0 = species.distance(genome.getValue());
						if (d0<d) {
							d = d0;
							id = species.id;
						}
						if (d<=config.species.acceptAlwaysCompability) {
							break;
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
			else {
				for (Entry<Integer, Genome> genome : genomes) {
					boolean specicated = false;
					int count = (int)((float)speciesList.size()*Gaussian()*percentage);
					count = Math.min(count, speciesList.size());
					for (int k = 0; k < Math.max(1, count); k++) {
						Species species = speciesList.getRandom();
						float d = species.distance(genome.getValue());
						if (d<config.species.compatibilityThreshold) {
							species.add(genome.getValue());
							specicated = true;
							break;
						}
					}
					if (specicated) {
						continue;
					}
					else {
						Species species0 = new Species(this, speciesList.nextId(), genome.getValue());
						speciesList.add(species0);
						species0.add(genome.getValue());
					}
				}
			}
		}
	}
	
	public void shuffleSpecies() {
		speciesList.shuffle();
	}
	public void shuffleGenomes() {
		genomes.shuffle();
	}
	
	public void reverseSpecies() {
		speciesList.reverse();
	}
	public void reverseGenomes() {
		genomes.reverse();
	}
	
	public void sortSpecies() {
		speciesList.getList().sort(speciesList.getComparator().reversed());
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
	
	@Deprecated
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
	@Deprecated
	public void initSpecicateFast(float percentage) {
		float d = -1;
		Genome center = genomes.getRandom();
		int count = (int)((float)genomes.size()*Gaussian()*percentage);
		count = Math.min(count, genomes.size());
		for (int k1 = 0; k1 < count; k1++) {
			Genome genome1 = genomes.getRandom();
			int count2 = (int)((float)genomes.size()*Gaussian()*percentage);
			count2 = Math.min(count, genomes.size());
			float d0 = 0;
			for (int k2 = 0; k2 < count2; k2++) {
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
	@Deprecated
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
	
	public static double nextGaussian() {
		double v1, v2, s;
		do {
			v1 = 2 * Math.random() -1;
			v2 = 2 * Math.random() -1;
			s = Math.pow(v1, 2) + Math.pow(v2, 2);
		} while (s >= 1 || s == 0);
		double multipier = Math.sqrt(-2 * Math.log(s)/s);
		return v1 * multipier;
	}
	public static double Gaussian() {
		double rand = nextGaussian();
		return Math.exp(rand);
	}
	
}
