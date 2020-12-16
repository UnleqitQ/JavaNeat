package org.neat.gene;

import org.neat.neat.Neat;

public abstract class RepresentativeGene {
	
	public final Neat neat;
	
	public final long innovationNumber;
	
	public RepresentativeGene(Neat neat, long l) {
		this.innovationNumber = l;
		this.neat = neat;
	}
	
	public long getInnovationNumber() {
		return innovationNumber;
	}
	
	public Neat getNeat() {
		return neat;
	}
	
	@Override
	public String toString() {
		return Long.toString(getInnovationNumber());
	}
	
}
