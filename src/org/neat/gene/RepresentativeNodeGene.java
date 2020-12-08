package org.neat.gene;

import org.neat.genome.*;
import org.neat.neat.*;

public class RepresentativeNodeGene extends RepresentativeGene {
	
	public final Neat neat;
	
	public final float x;
	
	public String title = "";
	
	public RepresentativeNodeGene(Neat neat, long innovationNumber, float x) {
		super(neat, innovationNumber);
		this.neat = neat;
		this.x = x;
	}
	
	public float getX() {
		return x;
	}
	
	public NodeGene createGene(Genome genome) {
		NodeGene gene = new NodeGene(this, genome);
		return gene;
	}
	
	@Override
	public String toString() {
		return getInnovationNumber()+":"+getX();
	}
	
}
