package org.neat.gene;

import org.neat.genome.*;
import org.neat.neat.*;

public abstract class BasicGene {
	
	public final Neat neat;
	public final Genome genome;
	
	public final RepresentativeGene representative;
	
	public BasicGene(RepresentativeGene representative, Genome genome) {
		this.genome = genome;
		this.neat = representative.neat;
		this.representative = representative;
	}
	
	public long getInnovationNumber() {
		return representative.innovationNumber;
	}
	
	public Neat getNeat() {
		return neat;
	}
	
	public RepresentativeGene getRepresentative() {
		return representative;
	}
	
	@Override
	public String toString() {
		return Long.toString(getInnovationNumber());
	}
	
}
