package org.neat.gene;

import org.neat.genome.*;
import org.neat.neat.*;

public class RepresentativeBiasGene extends RepresentativeNodeGene {
	
	public final Neat neat;
	
	public RepresentativeBiasGene(Neat neat, long innovationNumber, float x) {
		super(neat, innovationNumber, x);
		this.neat = neat;
	}
	
	@Override
	public BiasGene createGene(Genome genome) {
		BiasGene gene = new BiasGene(this, genome);
		return gene;
	}
	
	@Override
	public String toString() {
		return "B"+":"+Long.toString(getInnovationNumber());
	}
	
}
