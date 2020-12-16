package org.neat.gene;

import org.neat.genome.*;
import org.neat.neat.*;



public class ConnectionGene extends BasicGene {
	
	public final Neat neat;
	public final Genome genome;
	
	public final NodeGene fromGene;
	public final NodeGene toGene;
	public float weight = 0;
	public boolean enabled = true;
	
	public boolean switchGreaterLower = true;
	
	public ConnectionGene(RepresentativeConnectionGene representative, Genome genome, NodeGene fromGene, NodeGene toGene) {
		super(representative, genome);
		this.genome = genome;
		this.neat = representative.neat;
		if (switchGreaterLower && fromGene.x>toGene.x) {
			this.fromGene = toGene;
			this.toGene = fromGene;
		}
		else {
			this.fromGene = fromGene;
			this.toGene = toGene;
		}
		if (this.toGene.x<=this.fromGene.x) {
			throw new IllegalArgumentException("From-Node has to have a lower x than To-Node!");
		}
		if (fromGene.neat != neat || toGene.neat != neat) {
			throw new IllegalStateException("Instances of Neat need to match!");
		}
		if (representative.innovationNumber != fromGene.getInnovationNumber()+toGene.getInnovationNumber()*neat.maxNodes) {
			throw new IllegalStateException("Innovation Numbers need to match!");
		}
	}
	
	public RepresentativeConnectionGene getRepresentative() {
		return (RepresentativeConnectionGene) representative;
	}
	
	public NodeGene getFromGene() {
		return fromGene;
	}
	public NodeGene getToGene() {
		return toGene;
	}
	
	/*public void setFromGene(NodeGene gene) {
		if (this.neat != gene.neat) {
			throw new IllegalStateException("Instances of Neat need to match!");
		}
		if (fromGene.innovationNumber != gene.innovationNumber) {
			throw new IllegalStateException("Innovation Number can not change!");
		}
		this.fromGene = gene;
	}
	public void setToGene(NodeGene gene) {
		if (this.neat != gene.neat) {
			throw new IllegalStateException("Instances of Neat need to match!");
		}
		if (toGene.innovationNumber != gene.innovationNumber) {
			throw new IllegalStateException("Innovation Number can not change!");
		}
		this.toGene = gene;
	}*/
	
	public RepresentativeNodeGene getRepresentativeFromGene() {
		return ((RepresentativeConnectionGene)representative).fromGene;
	}
	public RepresentativeNodeGene getRepresentativeToGene() {
		return ((RepresentativeConnectionGene)representative).toGene;
	}
	
	public boolean isEqual(ConnectionGene other) {
		if (this.representative.innovationNumber == other.representative.innovationNumber) {
			if (this.enabled == other.enabled) {
				if (this.weight == other.weight) {
					return true;
				}
			}
		}
		return false;
	}
	public boolean isEqualExactlier(ConnectionGene other) {
		if (this.representative.innovationNumber == other.representative.innovationNumber) {
			if (this.enabled == other.enabled) {
				if (this.weight == other.weight) {
					if (this.fromGene.isEqual(other.fromGene)) {
						if (this.toGene.isEqual(other.toGene)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	public boolean isEqualExactliest(ConnectionGene other) {
		if (this.representative.innovationNumber == other.representative.innovationNumber) {
			if (this.enabled == other.enabled) {
				if (this.weight == other.weight) {
					if (this.fromGene == other.fromGene) {
						if (this.toGene == other.toGene) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	public float distance(ConnectionGene other) {
		float d = 0;
		d += Math.abs(this.weight- other.weight);
		if (this.enabled!=other.enabled) d += 1;
		return d*neat.config.species.compatibilityConnectionWeightCoefficient/2;
	}
	
	public ConnectionGene copy(Genome g, NodeGene from, NodeGene to) {
		if (from.genome != g | to.genome != g) {
			throw new IllegalArgumentException("Nodes have to belong to given Genome!");
		}
		ConnectionGene gene = neat.getConnection(g, from, to);
		gene.enabled = this.enabled;
		gene.weight = this.weight;
		return gene;
	}
	
	@Override
	public String toString() {
		return getInnovationNumber()+":"+(fromGene instanceof BiasGene?"B"+((BiasGene)fromGene).value+"/":"")+fromGene.getInnovationNumber()+"/"+fromGene.x+":"+toGene.getInnovationNumber()+"/"+toGene.x+":"+(enabled?"I":"O");
	}
	
}
