package org.neat.species;

import java.util.*;

import org.neat.gene.*;
import org.neat.genome.*;
import org.neat.libraries.*;
import org.neat.neat.*;

public class Species implements Comparable<Species> {
	
	public final Neat neat; 
	
	public Genome representative;
	public GenomeList genomes;
	
	public int id;
	
	public List<Float> fitnessHistory;
	public float fitness;
	
	public int Generation = 0;
	
	public Species(Neat neat, int id, Genome representative) {
		this.neat = neat;
		this.id = id;
		genomes = new GenomeList();
		this.representative = representative.copy();
		fitnessHistory = new ArrayList<>();
		for (int k = 0; k < neat.config.species.stagnationDuration; k++) {
			fitnessHistory.add(0f);
		}
	}
	
	public void add(Genome genome) {
		genomes.add(genome);
	}
	
	
	public void calcFitness() {
		this.fitness = neat.speciesFitnessFunction.apply(genomes.getList().toArray());
	}
	
	public float distance(Genome genome) {
		return representative.distance(genome);
	}
	
	public void setRepresentative(Genome genome) {
		representative = genome.copy();
	}
	
	public void newRepresentative() {
		float d = 0;
		for (Genome genome : genomes.getList()) {
			d += representative.distance(genome);
		}
		Genome middle = representative;
		for (Genome genome : genomes.getList()) {
			float d0 = 0;
			for (Genome genome2 : genomes.getList()) {
				d0 += genome.distance(genome2);
			}
			if (d < 0) {
				d = d0;
				middle = genome;
			}
			if (d0 < d) {
				d = d0;
				middle = genome;
			}
		}
		representative = middle.copy();
	}
	
	public boolean matches(Genome genome) {
		return distance(genome)<neat.config.species.compatibilityThreshold;
	}
	
	public void sortGenomes() {
		genomes.getList().sort(new Comparator<Genome>() {

			@Override
			public int compare(Genome o1, Genome o2) {
				return Float.compare(o1.fitness, o2.fitness);
			}
		}.reversed());
	}
	
	public Genome crossover(Species other) {
		sortGenomes();
		Genome genome1 = this.getCrossoverGenome();
		Genome genome2 = this.getCrossoverGenome();
		Genome genome;
		if (genome1.fitness > genome2.fitness) {
			genome = genome1.crossover(genome2);
		}
		else {
			genome = genome2.crossover(genome1);
		}
		return genome;
	}
	
	public Genome crossoverRand(Species other) {
		Genome genome1 = genomes.getRandom();
		Genome genome2 = other.genomes.getRandom();
		return genome1.crossover(genome2);
	}
	
	public Genome getCrossoverGenome() {
		int index = (int) Math.floor(genomes.size()*Math.pow(Math.random(), 1));
		return genomes.getList().get(index);
	}
	
	public List<Genome> cutGenomes(float percentage) {
		int Count = (int) Math.floor(percentage*genomes.size());
		sortGenomes();
		List<Genome> deprecated = new ArrayList<>();
		for (int k = 0; k < Count; k++) {
			deprecated.add(genomes.remove(genomes.getList().get(genomes.size()-1)));
		}
		return deprecated;
	}
	
	public void addFitness() {
		Generation++;
		fitnessHistory.add(0, fitness);
		fitnessHistory.remove(fitnessHistory.size()-1);
	}
	
	public boolean stagnate() {
		if (Generation < neat.config.species.stagnationDuration) {
			return false;
		}
		float fitness0 = fitnessHistory.get(neat.config.species.stagnationDuration-1);
		for (Float fitnessT : fitnessHistory) {
			if (fitnessT >= fitness0+neat.config.species.speciesProgressInterval) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int compareTo(Species o) {
		return Float.valueOf(fitness).compareTo(o.fitness);
	}
	
}
