package org.neat.gene;

import org.neat.activationFunction.*;
import org.neat.aggregationFunction.*;
import org.neat.genome.*;
import org.neat.neat.*;

public class NodeGene extends BasicGene {
	
	public final Neat neat;
	public final Genome genome;
	
	public final float x;
	
	public boolean enabled = true;
	public ActivationFunction activation;
	public boolean activationChanged = false;
	public AggregationFunction aggregation = new AggregationSum();
	public boolean aggregationChanged = false;
	
	public NodeGene(RepresentativeNodeGene representative, Genome genome) {
		super(representative, genome);
		activation = new ActivationSigmoid(representative.neat);
		this.genome = genome;
		this.neat = representative.neat;
		this.x = representative.x;
	}
	
	public float getX() {
		return x;
	}
	public RepresentativeNodeGene getRepresentative() {
		return (RepresentativeNodeGene) representative;
	}
	
	public boolean isEqual(NodeGene other) {
		if (this.getInnovationNumber() == other.getInnovationNumber()) {
			if (this.enabled == other.enabled) {
				if (this.activation == other.activation) {
					if (this.aggregation == other.aggregation) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public float distance(NodeGene other) {
		float d = 0;
		if (this.enabled != other.enabled) d += 1;
		if (this.activation != other.activation) d += 1;
		if (this.aggregation != other.aggregation) d += 1;
		return d*neat.config.species.compatibilityNodeWeightCoefficient/3;
	}
	
	
	public NodeGene copy(Genome g) {
		NodeGene gene = neat.getNode(getInnovationNumber(), g);
		gene.enabled = this.enabled;
		gene.activation = this.activation.copy();
		gene.aggregation = this.aggregation;
		return gene;
	}
	
	@Override
	public String toString() {
		return getInnovationNumber()+":"+getX()+":"+(enabled?"I":"O");
	}
	
}
