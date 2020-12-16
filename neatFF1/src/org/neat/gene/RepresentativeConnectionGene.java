package org.neat.gene;

import org.neat.genome.*;
import org.neat.neat.*;

public class RepresentativeConnectionGene extends RepresentativeGene {
	
	public final Neat neat;
	
	public final RepresentativeNodeGene fromGene;
	public final RepresentativeNodeGene toGene;
	
	public RepresentativeConnectionGene(Neat neat, int innovationNumber) {
		super(neat, innovationNumber);
		fromGene = neat.getRepresentativeNode(innovationNumber%neat.maxNodes);
		toGene = neat.getRepresentativeNode(innovationNumber/neat.maxNodes);
		this.neat = neat;
	}
	
	public RepresentativeConnectionGene(Neat neat, RepresentativeNodeGene fromGene, RepresentativeNodeGene toGene) {
		super(neat, fromGene.innovationNumber+toGene.innovationNumber*neat.maxNodes);
		this.neat = neat;
		this.fromGene = fromGene;
		this.toGene = toGene;
		if (fromGene.neat != neat || toGene.neat != neat) {
			throw new IllegalStateException("Instances of Neat need to match!");
		}
	}
	
	public RepresentativeNodeGene getFromGene() {
		return fromGene;
	}
	public RepresentativeNodeGene getToGene() {
		return toGene;
	}
	
	public ConnectionGene createGene(Genome genome, NodeGene from, NodeGene to) {
		ConnectionGene gene = new ConnectionGene(this, genome, from, to);
		return gene;
	}
	
	@Override
	public String toString() {
		return getInnovationNumber()+":"+(fromGene instanceof RepresentativeBiasGene?"B/":"")+fromGene.getInnovationNumber()+"/"+fromGene.x+":"+toGene.getInnovationNumber()+"/"+toGene.x;
	}
	
}
