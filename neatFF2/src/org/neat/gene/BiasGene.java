package org.neat.gene;

import org.neat.genome.*;
import org.neat.neat.*;

public class BiasGene extends NodeGene {
	
	public final Neat neat;
	public final Genome genome;
	
	public float value = 1;
	
	public BiasGene(RepresentativeBiasGene representative, Genome genome) {
		super(representative, genome);
		this.genome = genome;
		this.neat = representative.neat;
	}
	
	@Override
	public String toString() {
		return "B"+value+":"+Long.toString(getInnovationNumber());
	}

}
