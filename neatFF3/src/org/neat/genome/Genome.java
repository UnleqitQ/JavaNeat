package org.neat.genome;

import java.util.*;

import org.neat.activationFunction.*;
import org.neat.aggregationFunction.*;
import org.neat.gene.*;
import org.neat.libraries.Entry;
import org.neat.neat.*;

public class Genome implements Comparable<Genome> {
	
	
	/**
	 * Instance of Neat 
	 */
	public final Neat neat;
	
	/**
	 * Identification Number 
	 */
	public final int id;
	
	/**
	 * Fitness value<br>
	 * Adjust while use with Calculator 
	 */
	public float fitness = 0;
	
	/**
	 * Not recommended to use 
	 */
	public NodeList inputNodes = new NodeList();
	/**
	 * Not recommended to use 
	 */
	public NodeList outputNodes = new NodeList();
	/**
	 * Not recommended to use 
	 */
	public NodeList nodes = new NodeList();
	/**
	 * Not recommended to use 
	 */
	public ConnectionList connections;
	public final int inputCount;
	public final int outputCount;
	/**
	 * Activation Function of all Nodes<br>
	 * except Nodes with already changed Activation Functions
	 */
	public ActivationFunction activation;
	/**
	 * Aggregation Function of all Nodes<br>
	 * except Nodes with already changed Aggregation Functions
	 */
	public AggregationFunction aggregation = new AggregationSum();
	
	
	/**
	 * Constructor - Used by Neat
	 * @param neat Instance of Neat
	 * @param inputCount Quantity of input values
	 * @param outputCount Quantity of input values
	 * @param id {@linkplain #id}
	 */
	public Genome(Neat neat, int inputCount, int outputCount, int id) {
		if (!neat.isInitialized()) {
			throw new IllegalStateException("You first need to initialize Neat!");
		}
		activation = new ActivationSigmoid(neat);
		this.id = id;
		connections = new ConnectionList(neat);
		this.neat = neat;
		this.inputCount = inputCount;
		this.outputCount = outputCount;
		this.activation = neat.defaultActivationFunction;
		this.aggregation = neat.defaultAggregationFunction;
		for (int k = 0; k < inputCount; k++) {
			NodeGene gene = neat.getNode(k, this);
			inputNodes.add(gene);
			nodes.add(gene);
		}
		for (int k = 0; k < outputCount; k++) {
			NodeGene gene = neat.getNode(k+inputCount, this);
			outputNodes.add(gene);
			nodes.add(gene);
		}
		if (neat.bias) {
			BiasGene bias = neat.getBias(this);
			inputNodes.add(bias);
			nodes.add(bias);
		}
	}
	
	
	/**
	 * Currently only used to initialize mesh
	 */
	public void initialize() {
		mesh();
	}
	
	/**
	 * Creates new Connection
	 * @param node1 {@linkplain org.neat.gene.NodeGene First Node}
	 * @param node2 {@linkplain org.neat.gene.NodeGene Second Node}
	 */
	public void createLink(NodeGene node1, NodeGene node2) {
		if (connections.contains(node1, node2)) {
			return;
		}
		if (connections.contains(node2, node1)) {
			return;
		}
		try {
			ConnectionGene link = neat.getConnection(this, node1, node2);
			link.weight = (float) (Math.random()*2-1)*neat.config.mutate.newLinkWeightInterval;
			connections.add(link);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Used to mesh initial Network
	 */
	public void mesh() {
		if (neat.config.initGenome.meshGenome) {
			ConnectionList possible = possibleConnections();
			if (!neat.config.initGenome.fullMesh) {
				int remove = (int) Math.floor(possible.size()*(1-neat.config.initGenome.meshPercentage));
				for (int k = 0; k < remove; k++) {
					possible.removeRandom();
				}
			}
			for (Entry<Long, ConnectionGene> geneEntry : possible) {
				geneEntry.getValue().weight = (float) ((Math.random()*2-1)*neat.config.mutate.newLinkWeightInterval);
				connections.add(geneEntry.getValue());
			}
		}
	}
	
	
	private ConnectionList possibleConnections() {
		ConnectionList possible = new ConnectionList(neat);
		for (Entry<Long, NodeGene> node1 : nodes) {
			for (Entry<Long, NodeGene> node2 : nodes) {
				if (node1.getValue().x<node2.getValue().x) {
					if (!connections.contains(node1.getValue(), node2.getValue())) {
						possible.add(neat.getConnection(this, node1.getValue(), node2.getValue()));
					}
				}
			}
		}
		return possible;
	}
	
	
	/**
	 * Used to split Connection in the middle
	 * @param connection {@linkplain org.neat.gene.ConnectionGene Connection} to split
	 */
	public void SplitConnection(ConnectionGene connection) {
		if (connection.genome != this) {
			throw new IllegalArgumentException("Connection has to belong Genome!");
		}
		NodeGene node = neat.addNode((connection.fromGene.x+connection.toGene.x)/2, this);
		node.activation = this.activation.copy();
		node.aggregation = this.aggregation;
		connections.remove(connection);
		ConnectionGene link1 = neat.getConnection(this, connection.fromGene, node);
		ConnectionGene link2 = neat.getConnection(this, node, connection.toGene);
		link1.weight = connection.weight;
		link1.enabled = connection.enabled;
		link2.weight = 1;
		nodes.add(node);
		connections.add(link1);
		connections.add(link2);
	}
	
	public void SplitAndCombineConnections(ConnectionGene[] connections) {
		float xFrom = 0;
		float xTo = 1;
		Map<NodeGene, Float[]> fromNodes = new HashMap();
		Set<NodeGene> toNodes = new HashSet();
		for (ConnectionGene connection : connections) {
			if (connection.genome != this) {
				throw new IllegalArgumentException("Connection has to belong Genome!");
			}
			if (connection.fromGene.x>xFrom) xFrom = connection.fromGene.x;
			if (connection.toGene.x<xTo) xFrom = connection.toGene.x;
			if (!fromNodes.containsKey(connection.fromGene)) {
				fromNodes.put(connection.fromGene, new Float[] {0f, 0f});
			}
			if (connection.enabled) fromNodes.get(connection.fromGene)[0] += connection.weight;
			fromNodes.get(connection.fromGene)[1]++;
			toNodes.add(connection.toGene);
			this.connections.remove(connection);
		}
		if (xFrom>=xTo) {
			throw new IllegalArgumentException("The most left 'To'-Node has to have a greater x than the most right 'From'-Node");
		}
		float xNew = (xFrom+xTo)/2;
		NodeGene node = neat.addNode(xNew, this);
		node.activation = this.activation.copy();
		node.aggregation = this.aggregation;
		for (NodeGene fromNode : fromNodes.keySet()) {
			ConnectionGene link = neat.getConnection(this, fromNode, node);
			link.weight = fromNodes.get(fromNode)[0]/fromNodes.get(fromNode)[1];
			this.connections.add(link);
		}
		for (NodeGene toNode : toNodes) {
			ConnectionGene gene = neat.getConnection(this, node, toNode);
			gene.weight = 1;
			this.connections.add(gene);
		}
	}
	
	public void SplitAndCombineInConnections(NodeGene node) {
		List<ConnectionGene> connectionGenes = new ArrayList<>();
		float maxX = 0;
		for (Entry<Long, ConnectionGene> entry : connections) {
			if (entry.getValue().toGene == node) {
				maxX = Math.max(maxX, entry.getValue().fromGene.x);
				connectionGenes.add(entry.getValue());
			}
		}
		if (connectionGenes.size()<2) {
			return;
		}
		NodeGene gene = neat.addNode((maxX+node.x)/2, this);
		gene.activation = this.activation.copy();
		gene.aggregation = this.aggregation;
		nodes.add(gene);
		ConnectionGene con = neat.getConnection(this, gene, node);
		connections.add(con);
		con.weight = 1;
		for (ConnectionGene connectionGene : connectionGenes) {
			connections.remove(connectionGene);
			con = neat.getConnection(this, connectionGene.fromGene, gene);
			connections.add(con);
			con.weight = connectionGene.weight;
		}
	}
	public void SplitAndCombineOutConnections(NodeGene node) {
		List<ConnectionGene> connectionGenes = new ArrayList<>();
		float minX = 1;
		for (Entry<Long, ConnectionGene> entry : connections) {
			if (entry.getValue().fromGene == node) {
				minX = Math.min(minX, entry.getValue().toGene.x);
				connectionGenes.add(entry.getValue());
			}
		}
		if (connectionGenes.size()<2) {
			return;
		}
		NodeGene gene = neat.addNode((minX+node.x)/2, this);
		gene.activation = this.activation.copy();
		gene.aggregation = this.aggregation;
		nodes.add(gene);
		ConnectionGene con = neat.getConnection(this, node, gene);
		connections.add(con);
		con.weight = 1;
		for (ConnectionGene connectionGene : connectionGenes) {
			connections.remove(connectionGene);
			con = neat.getConnection(this, gene, connectionGene.toGene);
			connections.add(con);
			con.weight = connectionGene.weight;
		}
	}
	
	public void mutateCombineIns() {
		if (neat.config.mutate.combineInLinks) {
			int count = (int) Math.min(nodes.size(), neat.Gaussian()*neat.config.mutate.averageCombineInConnectionsPerMutate);
			for (int k = 0; k < count; k++) {
				NodeGene gene = nodes.getRandom();
				if (gene.x<=0) {
					continue;
				}
				SplitAndCombineInConnections(gene);
			}
		}
	}
	public void mutateCombineOuts() {
		if (neat.config.mutate.combineOutLinks) {
			int count = (int) Math.min(nodes.size(), neat.Gaussian()*neat.config.mutate.averageCombineOutConnectionsPerMutate);
			for (int k = 0; k < count; k++) {
				NodeGene gene = nodes.getRandom();
				if (gene.x>=1) {
					continue;
				}
				SplitAndCombineOutConnections(gene);
			}
		}
	}
	
	private int possibleLinksCount() {
		Map<Float, Integer> countPerX = new HashMap();
		List<Float> xVals = new ArrayList();
		for (float xVal : nodes.getNodesByX().keySet()) {
			countPerX.put(xVal, nodes.getNodesByX().get(xVal).size());
			xVals.add(xVal);
		}
		int count = 0;
		xVals.sort(new Comparator<Float>() {
			@Override
			public int compare(Float arg0, Float arg1) {
				return Float.compare(arg0, arg1);
			}
		});
		for (int k1 = 0; k1 < xVals.size()-1; k1++) {
			for (int k2 = k1+1; k2 < xVals.size(); k2++) {
				count += countPerX.get(xVals.get(k1))*countPerX.get(xVals.get(k2));
			}
		}
		return count-connections.size();
	}
	
	/**
	 * Used to randomly add {@linkplain org.neat.gene.NodeGene Nodes} during mutating
	 */
	public void mutateAddNode() {
		if (neat.config.mutate.addNode) {
			if (neat.config.mutate.randEveryPossibleNode) {
				ConnectionList toSplit = new ConnectionList(neat);
				try {
					for (Entry<Long, ConnectionGene> link : connections) {
						if (/*Math.random()<neat.config.mutate.addNodeProb && */Math.random()*connections.size()<neat.config.mutate.averageAddNodesPerMutate) {
							toSplit.add(link.getValue());
						}
					}
				}
				catch (ConcurrentModificationException e) {
					e.printStackTrace();
				}
				for (Entry<Long, ConnectionGene> connection : toSplit) {
					SplitConnection(connection.getValue());
				}
			}
			else {
				int count = (int) Math.min(connections.size(), Math.round(neat.config.mutate.averageAddNodesPerMutate*neat.Gaussian()));
				for (int k = 0; k < count; k++) {
					ConnectionGene link = connections.getRandom();
					SplitConnection(link);
				}
			}
		}
	}
	/**
	 * Used to randomly add already existing {@linkplain org.neat.gene.NodeGene Nodes} during mutating
	 */
	public void mutateAddExistingNode() {
		mutateAddExistingNode(1);
	}
	/**
	 * Used to randomly add already existing {@linkplain org.neat.gene.NodeGene Nodes} during mutating
	 * @param factor used to change the range of weights
	 */
	public void mutateAddExistingNode(float factor) {
		if (neat.config.mutate.addExistingNode) {
			if (neat.config.mutate.randEveryPossibleExistingNode) {
				Set<Long> possibleNodesInno = new HashSet();
				for (Entry<Long, RepresentativeNodeGene> node : neat.nodes) {
					if (!nodes.contains(node.getValue())) {
						possibleNodesInno.add(node.getKey());
					}
				}
				for (long inno : possibleNodesInno) {
					if (/*Math.random()<neat.config.mutate.addExistingNodeProb && */Math.random()*possibleNodesInno.size()<neat.config.mutate.averageAddExistingNodesPerMutate) {
						NodeGene gene = neat.getNode(inno, this);
						gene.activation = this.activation.copy();
						gene.aggregation = this.aggregation;
						List<Long> fromInno = new ArrayList();
						List<Long> toInno = new ArrayList();
						for (Entry<Long, NodeGene> node : nodes) {
							if (node.getValue().x < gene.x) {
								fromInno.add(node.getValue().getInnovationNumber());
							}
							if (node.getValue().x > gene.x) {
								toInno.add(node.getValue().getInnovationNumber());
							}
						}
						ConnectionGene link1 = neat.getConnection(this, nodes.get(fromInno.get((int) Math.floor(Math.random()*fromInno.size()))), gene);
						ConnectionGene link2 = neat.getConnection(this, gene, nodes.get(toInno.get((int) Math.floor(Math.random()*toInno.size()))));
						link1.weight = (float) ((Math.random()*2-1)*neat.config.mutate.newLinkWeightInterval*factor);
						link2.weight = (float) ((Math.random()*2-1)*neat.config.mutate.newLinkWeightInterval*factor);
						nodes.add(gene);
						connections.add(link1);
						connections.add(link2);
					}
				}
			}
			else {
				int count = (int) Math.min(neat.nodes.size()-nodes.size(), Math.round(neat.config.mutate.averageAddExistingNodesPerMutate*neat.Gaussian()));
				for (int k = 0; k < count; k++) {
					for (int tries = 0; tries < 1000; tries++) {
						RepresentativeNodeGene gene = neat.nodes.getRandom();
						if (nodes.contains(gene.innovationNumber)) {
							continue;
						}
						if (!nodes.contains(gene)) {
							NodeGene node = neat.getNode(gene, this);
							List<Float> xValsFrom = new ArrayList();
							List<Float> xValsTo = new ArrayList();
							for (float xVal : nodes.getNodesByX().keySet()) {
								if (xVal<gene.x) {
									xValsFrom.add(xVal);
								}
								else if (xVal>gene.x) {
									xValsTo.add(xVal);
								}
							}
							float xFrom = xValsFrom.get((int) Math.floor(Math.random()*xValsFrom.size()));
							float xTo = xValsTo.get((int) Math.floor(Math.random()*xValsTo.size()));
							List<NodeGene> nodesFrom = nodes.getNodesByX().get(xFrom);
							List<NodeGene> nodesTo = nodes.getNodesByX().get(xTo);
							ConnectionGene link1 = neat.getConnection(this, nodesFrom.get((int) Math.floor(Math.random()*nodesFrom.size())), node);
							ConnectionGene link2 = neat.getConnection(this, node, nodesTo.get((int) Math.floor(Math.random()*nodesTo.size())));
							link1.weight = (float) ((Math.random()*2-1)*neat.config.mutate.newLinkWeightInterval*factor);
							link2.weight = (float) ((Math.random()*2-1)*neat.config.mutate.newLinkWeightInterval*factor);
							nodes.add(node);
							connections.add(link1);
							connections.add(link2);
						}
					}
				}
			}
		}
	}
	/**
	 * Used to randomly add {@linkplain org.neat.gene.ConnectionGene Connections} during mutating
	 */
	public void mutateAddLink() {
		mutateAddLink(1);
	}
	/**
	 * Used to randomly add {@linkplain org.neat.gene.ConnectionGene Connections} during mutating
	 * @param factor used to change the range of weights
	 */
	public void mutateAddLink(float factor) {
		if (neat.config.mutate.addLink) {
			if (neat.config.mutate.randEveryPossibleLink) {
				for (Entry<Long, NodeGene> node1 : nodes) {
					for (Entry<Long, NodeGene> node2 : nodes) {
						if (node1.getValue().x != node2.getValue().x) {
							NodeGene from;
							NodeGene to;
							if (node1.getValue().x < node2.getValue().x) {
								from = node1.getValue();
								to = node2.getValue();
							}
							else {
								from = node2.getValue();
								to = node1.getValue();
							}
							if (!connections.contains(from, to)) {
								if (/*Math.random()<neat.config.mutate.addLinkProb && */Math.random()*possibleLinksCount() < neat.config.mutate.averageAddConnectionsPerMutate) {
									ConnectionGene gene = neat.getConnection(this, from, to);
									gene.weight = (float) ((Math.random()*2-1)*neat.config.mutate.newLinkWeightInterval*factor);
									connections.add(gene);
								}
							}
						}
					}
				}
			}
			else {
				int Count = (int) Math.min(Math.floor(neat.config.mutate.averageAddConnectionsPerMutate*neat.Gaussian()), possibleLinksCount());
				for (int k = 0; k < Count; k++) {
					List<Float> xVals = new ArrayList();
					for (float xVal : nodes.getNodesByX().keySet()) {
						xVals.add(xVal);
					}
					xVals.sort(new Comparator<Float>() {
						@Override
						public int compare(Float arg0, Float arg1) {
							return Float.compare(arg0, arg1);
						}
					});
					int xIndexFrom = (int) Math.floor((xVals.size()-1)*Math.random());
					NodeGene fromNode = nodes.getNodesByX().get(xVals.get(xIndexFrom)).get((int) Math.floor(Math.random()*nodes.getNodesByX().get(xVals.get(xIndexFrom)).size()));
					for (int tries = 0; tries < 1000; tries++) {
						int xIndexTo = (int) Math.floor((xVals.size()-xIndexFrom-1)*Math.random())+xIndexFrom+1;
						NodeGene toNode = nodes.getNodesByX().get(xVals.get(xIndexTo)).get((int) Math.floor(Math.random()*nodes.getNodesByX().get(xVals.get(xIndexTo)).size()));
						if (!connections.contains(fromNode, toNode)) {
							ConnectionGene gene = neat.getConnection(this, fromNode, toNode);
							gene.weight = (float) ((Math.random()*2-1)*neat.config.mutate.newLinkWeightInterval*factor);
						}
					}
				}
			}
		}
	}
	/**
	 * Used to randomly enable or disable {@linkplain org.neat.gene.NodeGene Nodes} during mutating
	 */
	public void mutateToggleNode() {
		if (neat.config.mutate.toggleNode) {
			if (neat.config.mutate.randEveryPossibleNodeToggle) {
				for (Entry<Long, NodeGene> node : nodes) {
					if (Math.random()*nodes.size()<neat.config.mutate.averageToggleNodesPerMutate/*Math.random()<neat.config.mutate.toggleNodeProb*/) {
						node.getValue().enabled = !node.getValue().enabled;
					}
				}
			}
			else {
				int Count = (int) Math.min(Math.floor(neat.config.mutate.averageToggleNodesPerMutate*neat.Gaussian()), nodes.size());
				for (int k = 0; k < Count; k++) {
					NodeGene gene = nodes.getRandom();
					if (gene.x>0 && gene.x<1) {
						gene.enabled = !gene.enabled;
					}
				}
			}
		}
	}
	/**
	 * Used to randomly enabled or disable {@linkplain org.neat.gene.ConnectionGene Connections} during mutating
	 */
	public void mutateToggleLink() {
		if (neat.config.mutate.toggleLink) {
			if (neat.config.mutate.randEveryPossibleLinkToggle) {
				for (Entry<Long, ConnectionGene> connection : connections) {
					if (Math.random()*connections.size()<neat.config.mutate.averageToggleConnectionsPerMutate/*Math.random()<neat.config.mutate.toggleLinkProb*/) {
						connection.getValue().enabled = !connection.getValue().enabled;
					}
				}
			}
			else {
				int Count = (int) Math.min(Math.floor(neat.config.mutate.averageToggleConnectionsPerMutate*neat.Gaussian()), connections.size());
				for (int k = 0; k < Count; k++) {
					ConnectionGene gene = connections.getRandom();
					gene.enabled = !gene.enabled;
				}
			}
		}
	}
	/**
	 * Used to randomly change the weights of the {@linkplain org.neat.gene.ConnectionGene Connections} during mutating
	 */
	public void mutateWeight() {
		mutateWeight(1);
	}
	/**
	 * Used to randomly replace the weights of the {@linkplain org.neat.gene.ConnectionGene Connections} during mutating
	 */
	public void mutateNewWeight() {
		mutateNewWeight(1);
	}
	/**
	 * Used to randomly change the weights of the {@linkplain org.neat.gene.ConnectionGene Connections} during mutating
	 * @param factor used to change the range of weights
	 */
	public void mutateWeight(float factor) {
		if (neat.config.mutate.mutateWeight) {
			for (Entry<Long, ConnectionGene> connection : connections) {
				if (Math.random()<neat.config.mutate.mutateWeightProb) {
					connection.getValue().weight += (Math.random()*2-1)*neat.config.mutate.mutateWeightInterval*factor;
					if (neat.config.mutate.hasWeightBorders) {
						if (Math.abs(connection.getValue().weight) > neat.config.mutate.weightBorders) {
							connection.getValue().weight = neat.config.mutate.weightBorders * Math.signum(connection.getValue().weight);
						}
					}
				}
			}
		}
	}
	/**
	 * Used to randomly replace the weights of the {@linkplain org.neat.gene.ConnectionGene Connections} during mutating
	 * @param factor used to change the range of weights
	 */
	public void mutateNewWeight(float factor) {
		if (neat.config.mutate.newWeight) {
			for (Entry<Long, ConnectionGene> connection : connections) {
				if (Math.random()<neat.config.mutate.newWeightProb) {
					connection.getValue().weight = (float) ((Math.random()*2-1)*neat.config.mutate.newWeightInterval)*factor;
				}
			}
		}
	}
	/**
	 * Used to randomly change the {@linkplain org.neat.activationFunction.ActivationFunction Activation Functions} of {@linkplain org.neat.gene.NodeGene Nodes} during mutating
	 */
	public void mutateNodeActivationFunction() {
		if (neat.config.mutate.nodeActivation) {
			if (neat.config.mutate.randEveryPossibleNodeActivation) {
				for (Entry<Long, NodeGene> node : nodes) {
					if (node.getValue().x<0 && Math.random()*(nodes.size()-inputNodes.size())<neat.config.mutate.averageNewNodesActivationPerMutate/*Math.random()<neat.config.mutate.nodeActivationProb*/) {
						Object[] array = neat.activationFunctions.toArray();
						node.getValue().activation = (ActivationFunction) array[(int) Math.floor(Math.random()*array.length)];
						node.getValue().activationChanged = true;
					}
				}
			}
			else {
				int Count = (int) Math.floor(neat.config.mutate.averageNewNodesActivationPerMutate*neat.Gaussian())/inputNodes.size()*nodes.size();
				Object[] array = neat.activationFunctions.toArray();
				for (int k = 0; k < Count; k++) {
					NodeGene gene = nodes.getRandom();
					gene.activation = (ActivationFunction) array[(int) Math.floor(Math.random()*array.length)];
					gene.activationChanged = true;
				}
			}
		}
	}
	public void mutateNodeActivationParameters() {
		if (neat.config.mutate.nodeActivationParameters) {
			if (neat.config.mutate.randEveryPossibleNodeActivationParameters) {
				for (Entry<Long, NodeGene> node : nodes) {
					if (node.getValue().x<0 && Math.random()*(nodes.size()-inputNodes.size())<neat.config.mutate.averageMutateNodesActivationPerMutate/*Math.random()<neat.config.mutate.nodeActivationProb*/) {
						node.getValue().activation.mutate();
						node.getValue().activationChanged = true;
					}
				}
			}
			else {
				int Count = (int) Math.floor(neat.config.mutate.averageMutateNodesActivationPerMutate*neat.Gaussian())/inputNodes.size()*nodes.size();
				Object[] array = neat.activationFunctions.toArray();
				for (int k = 0; k < Count; k++) {
					NodeGene gene = nodes.getRandom();
					gene.activation.mutate();
					gene.activationChanged = true;
				}
			}
		}
	}
	/**
	 * Used to randomly change the {@linkplain org.neat.aggregationFunction.AggregationFunction Aggregation Functions} of {@linkplain org.neat.gene.NodeGene Nodes} during mutating
	 */
	public void mutateNodeAggregationFunction() {
		if (neat.config.mutate.nodeAggregation) {
			if (neat.config.mutate.randEveryPossibleNodeAggregation) {
				for (Entry<Long, NodeGene> node : nodes) {
					if (node.getValue().x>0 && Math.random()*(nodes.size()-inputNodes.size())<neat.config.mutate.averageNewNodesAggregatonPerMutate/*Math.random()<neat.config.mutate.nodeAggregationProb*/) {
						Object[] array = neat.aggregationFunctions.toArray();
						node.getValue().aggregation = (AggregationFunction) array[(int) Math.floor(Math.random()*array.length)];
						node.getValue().aggregationChanged = true;
					}
				}
			}
			else {
				int Count = (int) Math.floor(neat.config.mutate.averageNewNodesAggregatonPerMutate*neat.Gaussian())/inputNodes.size()*nodes.size();
				Object[] array = neat.aggregationFunctions.toArray();
				for (int k = 0; k < Count; k++) {
					NodeGene gene = nodes.getRandom();
					gene.aggregation = (AggregationFunction) array[(int) Math.floor(Math.random()*array.length)];
					gene.aggregationChanged = true;
				}
			}
		}
	}
	/**
	 * Used to randomly change the {@linkplain org.neat.activationFunction.ActivationFunction Activation Function} of the {@linkplain org.neat.genome.Genome Genome}
	 */
	public void mutateGenomeActivationFunction() {
		if (neat.config.mutate.genomeActivation) {
			if (Math.random()<neat.config.mutate.genomeActivationProb) {
				Object[] array = neat.activationFunctions.toArray();
				this.activation = (ActivationFunction) array[(int) Math.floor(Math.random()*array.length)];
				for (NodeGene gene : nodes.getList()) {
					if (!gene.activationChanged) {
						gene.activation = this.activation.copy();
					}
				}
			}
		}
	}
	public void mutateGenomeActivationParameters() {
		if (neat.config.mutate.genomeActivationParameters) {
			if (Math.random()<neat.config.mutate.genomeActivationParametersProb) {
				activation.mutate();
				for (NodeGene gene : nodes.getList()) {
					if (!gene.activationChanged) {
						gene.activation = this.activation.copy();
					}
				}
			}
		}
	}
	/**
	 * Used to randomly change the {@linkplain org.neat.aggregationFunction.AggregationFunction Aggregation Function} of the {@linkplain org.neat.genome.Genome Genome}
	 */
	public void mutateGenomeAggregationFunction() {
		if (neat.config.mutate.genomeAggregation) {
			if (Math.random()<neat.config.mutate.genomeAggregationProb) {
				Object[] array = neat.aggregationFunctions.toArray();
				this.aggregation = (AggregationFunction) array[(int) Math.floor(Math.random()*array.length)];
				for (NodeGene gene : nodes.getList()) {
					if (!gene.aggregationChanged) {
						gene.aggregation = this.aggregation;
					}
				}
			}
		}
	}
	
	/**
	 * Used to mutate the {@linkplain org.neat.genome.Genome Genome}
	 */
	public void mutate() {
		mutateGenomeActivationFunction();
		mutateGenomeAggregationFunction();
		mutateGenomeActivationParameters();
		mutateAddLink();
		mutateAddNode();
		mutateCombineIns();
		mutateCombineOuts();
		mutateAddExistingNode();
		mutateNewWeight();
		mutateWeight();
		mutateToggleLink();
		mutateToggleNode();
		mutateNodeActivationFunction();
		mutateNodeAggregationFunction();
		mutateNodeActivationParameters();
	}
	/**
	 * Used to mutate the {@linkplain org.neat.genome.Genome Genome}
	 * @param factor used to change the range of weights
	 */
	public void mutate(float factor) {
		mutateGenomeActivationFunction();
		mutateGenomeAggregationFunction();
		mutateGenomeActivationParameters();
		mutateAddLink(factor);
		mutateAddNode();
		mutateCombineIns();
		mutateCombineOuts();
		mutateAddExistingNode(factor);
		mutateNewWeight(factor);
		mutateWeight(factor);
		mutateToggleLink();
		mutateToggleNode();
		mutateNodeActivationParameters();
		mutateNodeAggregationFunction();
		mutateNodeActivationParameters();
	}
	
	
	
	
	/**
	 * Used to calculate the difference of another {@linkplain org.neat.genome.Genome Genome}
	 * @param other Genome to compare with
	 * @return value to describe the difference
	 */
	public float distance(Genome other) {
		float outputDistance = 0;
		for (long innovation : outputNodes.getInnovationSet()) {
			NodeGene node1 = this.outputNodes.get(innovation);
			NodeGene node2 = other.outputNodes.get(innovation);
			outputDistance += node1.distance(node2);
		}
		float hiddenNodesDistance = 0;
		float disjointNodes = 0;
		for (Entry<Long, NodeGene> node1 : nodes) {
			if (node1.getValue().x>0 && node1.getValue().x<1) {
				if (other.nodes.contains(node1.getValue())) {
					NodeGene node2 = other.nodes.get(node1.getValue());
					hiddenNodesDistance += node1.getValue().distance(node2);
				}
				else {
					disjointNodes++;
				}
			}
		}
		for (Entry<Long, NodeGene> node : other.nodes) {
			if (node.getValue().x>0 && node.getValue().x<1) {
				if (!nodes.contains(node.getValue())) {
					disjointNodes++;
				}
			}
		}
		hiddenNodesDistance /= nodes.size()+other.nodes.size()-2*(inputCount+outputCount);
		float connectionDistance = 0;
		float disjointConnections = 0;
		for (Entry<Long, ConnectionGene> connection1 : connections) {
			if (other.connections.contains(connection1.getValue())) {
				ConnectionGene connection2 = other.connections.get(connection1.getValue());
				connectionDistance += connection1.getValue().distance(connection2);
			}
			else {
				disjointConnections+=Math.sqrt(Math.abs(connection1.getValue().weight/neat.config.mutate.weightBorders));
			}
		}
		for (Entry<Long, ConnectionGene> connection : other.connections) {
			if (!connections.contains(connection.getValue())) {
				disjointConnections+=Math.sqrt(Math.abs(connection.getValue().weight/neat.config.mutate.weightBorders));
			}
		}
		float d = 0;
		d += outputDistance;
		d += hiddenNodesDistance;
		d += connectionDistance;
		d += disjointNodes*neat.config.species.compatibilityDisjointNodeCoefficient;
		d += disjointConnections*neat.config.species.compatibilityDisjointConnectionCoefficient;
		return d;
	}
	
	/**
	 * Generates a combination of both {@linkplain org.neat.genome.Genome Genomes}
	 * @param other Genome to combine with
	 * @return Created Child
	 */
	public Genome crossover(Genome other) {
		Genome genome = new Genome(neat, inputCount, outputCount, neat.genomes.nextId);
		if (Math.random()<neat.config.crossover.takeSecondActivation) {
			genome.activation = other.activation.copy();
		}
		else {
			genome.activation = this.activation.copy();
		}
		if (Math.random()<neat.config.crossover.takeSecondAggregation) {
			genome.aggregation = other.aggregation;
		}
		else {
			genome.aggregation = this.aggregation;
		}
		ConnectionList newConnections = new ConnectionList(neat);
		// create copy of this ConnectionList (equal Connections)
		for (Entry<Long, ConnectionGene> connection : connections) {
			newConnections.add(connection.getValue());
		}
		// add copy of other ConnectionList and replace with probability (equal Connections)
		for (Entry<Long, ConnectionGene> connection : other.connections) {
			if (newConnections.contains(connection.getValue())) {
				if (Math.random()<neat.config.crossover.takeSecondConnection) {
					newConnections.set(connection.getValue());
				}
			}
			else {
				newConnections.add(connection.getValue());
			}
		}
		
		// thin out List of Connections
		if (neat.config.crossover.thinConnections) {
			if (neat.config.crossover.thinViaPercentage) {
				int removeCnt = (int) Math.floor(newConnections.size()*neat.config.crossover.thinProbOrPerc);
				for (int k = 0; k < removeCnt; k++) {
					int index = (int) Math.floor(newConnections.size()*Math.random());
					newConnections.remove(newConnections.getList().get(index));
				}
			}
			else {
				List<ConnectionGene> removeGenes = new ArrayList<>();
				/*Iterator<Entry<Long, ConnectionGene>> itr = newConnections.iterator();
				while (itr.hasNext()) {
					Entry<Long, ConnectionGene> entry = itr.next();
					if (Math.random()<neat.config.crossover.thinProbOrPerc) {
						itr.remove();
					}
				}*/
				for (Entry<Long,ConnectionGene> entry : newConnections) {
					if (Math.random()<neat.config.crossover.thinProbOrPerc) {
						removeGenes.add(entry.getValue());
					}
				}
				for (ConnectionGene connectionGene : removeGenes) {
					newConnections.remove(connectionGene);
				}
			}
		}
		
		// Create NodeList of needed Nodes
		NodeList newNodes = new NodeList();
		// NodeList newInputs = new NodeList();
		for (Entry<Long, ConnectionGene> connection : newConnections) {
			if (!newNodes.contains(connection.getValue().fromGene)) {
				if (this.nodes.contains(connection.getValue().fromGene)) {
					if (other.nodes.contains(connection.getValue().fromGene)) {
						if (Math.random()<neat.config.crossover.takeSecondNode) {
							newNodes.add(other.nodes.get(connection.getValue().fromGene));
						}
						else {
							newNodes.add(this.nodes.get(connection.getValue().fromGene));
						}
					}
					else {
						newNodes.add(this.nodes.get(connection.getValue().fromGene));
					}
				}
				else {
					if (other.nodes.contains(connection.getValue().fromGene)) {
						newNodes.add(other.nodes.get(connection.getValue().fromGene));
					}
					else {
						newNodes.add(neat.getNode(connection.getValue().fromGene, genome));
					}
					
				}
			}
			if (this.nodes.contains(connection.getValue().toGene)) {
				if (other.nodes.contains(connection.getValue().toGene)) {
					if (Math.random()<neat.config.crossover.takeSecondNode) {
						newNodes.add(other.nodes.get(connection.getValue().toGene));
					}
					else {
						newNodes.add(this.nodes.get(connection.getValue().toGene));
					}
				}
				else {
					newNodes.add(this.nodes.get(connection.getValue().toGene));
				}
			}
			else {
				if (other.nodes.contains(connection.getValue().toGene)) {
					newNodes.add(other.nodes.get(connection.getValue().toGene));
				}
				else {
					newNodes.add(neat.getNode(connection.getValue().toGene, genome));
				}
			}
		}
		for (Entry<Long, NodeGene> node : newNodes) {
			NodeGene gene = node.getValue().copy(genome);
			if (!gene.activationChanged) {
				gene.activation = genome.activation;
			}
			if (!gene.aggregationChanged) {
				gene.aggregation = genome.aggregation;
			}
			genome.nodes.set(gene);
			if (gene.x <= 0) {
				genome.inputNodes.set(gene);
			}
			if (gene.x >= 1) {
				genome.outputNodes.set(gene);
			}
		}
		for (Entry<Long, NodeGene> node : inputNodes) {
			if (!newNodes.contains(node.getValue())) {
				if (Math.random()<neat.config.crossover.takeSecondNode) {
					genome.nodes.set(other.nodes.get(node.getValue()).copy(genome));
					genome.inputNodes.set(other.nodes.get(node.getValue()).copy(genome));
				}
				else {
					genome.nodes.set(this.nodes.get(node.getValue()).copy(genome));
					genome.inputNodes.set(this.nodes.get(node.getValue()).copy(genome));
				}
			}
		}
		for (Entry<Long, NodeGene> node : outputNodes) {
			if (!newNodes.contains(node.getValue())) {
				if (Math.random()<neat.config.crossover.takeSecondNode) {
					genome.nodes.set(other.nodes.get(node.getValue()).copy(genome));
					genome.outputNodes.set(other.nodes.get(node.getValue()).copy(genome));
				}
				else {
					genome.nodes.set(this.nodes.get(node.getValue()).copy(genome));
					genome.outputNodes.set(this.nodes.get(node.getValue()).copy(genome));
				}
			}
		}
		for (Entry<Long, ConnectionGene> connection : newConnections) {
			ConnectionGene gene = connection.getValue().copy(genome, genome.nodes.get(connection.getValue().fromGene), genome.nodes.get(connection.getValue().toGene));
			genome.connections.add(gene);
		}
		return genome;
	}
	
	/**
	 * Creates a copy of this Genome
	 * @return Copy
	 */
	public Genome copy() {
		Genome genome = new Genome(neat, inputCount, outputCount, id);
		genome.initialize();
		genome.nodes = nodes.copy(genome);
		genome.inputNodes.clear();
		genome.outputNodes.clear();
		for (Entry<Long, NodeGene> gene : genome.nodes) {
			if (gene.getValue().x<=0) {
				genome.inputNodes.add(gene.getValue());
			}
			if (gene.getValue().x>=1) {
				genome.outputNodes.add(gene.getValue());
			}
		}
		genome.activation = activation.copy();
		genome.aggregation = aggregation;
		genome.connections = connections.copy(genome, genome.nodes);
		return genome;
	}
	
	/**
	 * Creates a copy of this Genome with new ID
	 * @return Copy
	 */
	public Genome copyNew() {
		Genome genome = new Genome(neat, inputCount, outputCount, neat.genomes.nextId);
		genome.initialize();
		genome.nodes = nodes.copy(genome);
		genome.inputNodes.clear();
		genome.outputNodes.clear();
		for (Entry<Long, NodeGene> gene : genome.nodes) {
			if (gene.getValue().x<=0) {
				genome.inputNodes.add(gene.getValue());
			}
			if (gene.getValue().x>=1) {
				genome.outputNodes.add(gene.getValue());
			}
		}
		genome.activation = activation;
		genome.aggregation = aggregation;
		genome.connections = connections.copy(genome, genome.nodes);
		return genome;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Genome)) {
			return false;
		}
		Genome genome = (Genome) obj;
		if (genome.id!=this.id) {
			return false;
		}
		if (genome.neat!=this.neat) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		String string = "\n";
		string += nodes.toStringX();
		string += "\n\n";
		string += connections;
		string += "\n";
		return "";
	}


	@Override
	public int compareTo(Genome o) {
		return Float.valueOf(fitness).compareTo(o.fitness);
	}
	
}
