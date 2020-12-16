package org.neat.calculate;

import java.util.*;

import org.neat.activationFunction.*;
import org.neat.aggregationFunction.*;
import org.neat.gene.*;
import org.neat.genome.*;

/**
 * @author Quentin
 *
 */
public class Calculator {
	
	public Genome genome;
	/**
	 * Not recommended to use 
	 */
	public CalcNodeList inputs = new CalcNodeList();
	/**
	 * Not recommended to use 
	 */
	public CalcNodeList outputs = new CalcNodeList();
	/**
	 * Not recommended to use 
	 */
	public CalcNodeList nodes = new CalcNodeList();
	
	public Calculator(Genome genome) {
		this.genome = genome;
	}
	
	/**
	 * Only used by Neat,
	 * needed if Calculator is manually created
	 */
	public void initialize() {
		for (NodeGene gene : genome.nodes.getList()) {
			boolean input = false;
			if (gene.x <= 0) {
				input = true;
			}
			CalcNode node = new CalcNode(gene.getInnovationNumber(), gene.enabled, gene.activation, gene.aggregation, input, gene.x);
			if (gene.x <= 0) {
				inputs.add(node);
			}
			if (gene.x >= 1) {
				outputs.add(node);
			}
			if (gene instanceof BiasGene) {
				node.value = ((BiasGene) gene).value;
			}
			nodes.add(node);
		}
		for (ConnectionGene gene : genome.connections.getList()) {
			CalcConnection connection = new CalcConnection(gene.getInnovationNumber(), nodes.get(gene.fromGene.getInnovationNumber()), gene.weight, gene.enabled);
			nodes.get(gene.toGene.getInnovationNumber()).connections.add(connection);
		}
	}
	
	/**
	 * Applies the input values to the Input Nodes<br>
	 * The keys of the Map declare the innovation number<br>
	 * The input Nodes are the first created nodes, their innovations go from 0 to the count-1<br>
	 * A node whose value is not given keeps the previous or 0
	 * @param values Map with Innovation Number - Value
	 */
	public void setInputValues(Map<Long, Float> values) {
		for (long inno : values.keySet()) {
			inputs.get(inno).value = values.get(inno);
		}
		for (CalcNode node : nodes.getSet()) {
			node.calculated = false;
		}
	}
	
	/**
	 * Used to calculate the values of all Nodes<br>
	 * Automatically used in {@linkplain #getOutputs}
	 */
	public void calculate() {
		/*for (CalcNode node : nodes.getSet()) {
			node.calculated = false;
		}*/
		for (CalcNode node : nodes.getSet()) {
			node.calculate();
		}
	}
	
	
	/**
	 * Returns the calculated values
	 * The key is the innovation number of the Output Node
	 * @return Map with Innovation Number - Value
	 */
	public Map<Long, Float> getOutputs() {
		calculate();
		Map<Long, Float> values = new HashMap();
		for (CalcNode node : outputs.getSet()) {
			values.put(node.innovationNumber, node.value);
		}
		return values;
	}
	
	
	
}
