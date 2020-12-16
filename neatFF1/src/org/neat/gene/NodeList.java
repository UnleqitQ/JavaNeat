package org.neat.gene;

import java.util.*;
import java.util.function.Consumer;

import org.neat.genome.*;
import org.neat.libraries.*;

public class NodeList implements Iterable<Entry<Long, NodeGene>> {
	
	//Set<NodeGene> nodeSet = new HashSet();
	Set<Long> innovationSet = new HashSet();
	List<NodeGene> nodeList = new ArrayList();
	Map<Long, NodeGene> nodeMap = new HashMap();
	Map<Float, List<NodeGene>> nodesByX = new HashMap();
	public boolean exceptionAlreadyExists = false;
	public boolean exceptionRemoveNonExist = false;
	public boolean exceptionExistsNot = false;
	
	
	public void clear() {
		innovationSet.clear();
		nodeList.clear();
		nodeMap.clear();
		nodesByX.clear();
	}
	
	public NodeGene add(NodeGene gene) {
		if (innovationSet.contains(gene.getInnovationNumber())) {
			if (exceptionAlreadyExists) {
				throw new IllegalArgumentException("Gene already exists!");
			}
			return gene;
		}
		//nodeSet.add(gene);
		innovationSet.add(gene.getInnovationNumber());
		if (!nodesByX.containsKey(gene.x)) {
			nodesByX.put(gene.x, new ArrayList());
		}
		nodesByX.get(gene.x).add(gene);
		nodeList.add(gene);
		nodeMap.put(gene.getInnovationNumber(), gene);
		return gene;
	}
	
	public NodeGene remove(NodeGene gene) {
		if (contains(gene)) {
			innovationSet.remove(gene.getInnovationNumber());
			NodeGene exactGene = nodeMap.remove(gene.getInnovationNumber());
			nodeList.remove(exactGene);
			nodesByX.get(gene.x).remove(gene);
			if (nodesByX.get(gene.x).size()==0) {
				nodesByX.remove(gene.x);
			}
			//nodeSet.remove(exactGene);
			return exactGene;
		}
		if (exceptionRemoveNonExist) {
			throw new IllegalArgumentException("Gene does not exist!");
		}
		return gene;
	}
	public NodeGene remove(long innovationNumber) {
		if (contains(innovationNumber)) {
			innovationSet.remove(innovationNumber);
			NodeGene exactGene = nodeMap.remove(innovationNumber);
			nodeList.remove(exactGene);
			nodesByX.get(exactGene.x).remove(exactGene);
			if (nodesByX.get(exactGene.x).size()==0) {
				nodesByX.remove(exactGene.x);
			}
			//nodeSet.remove(exactGene);
			return exactGene;
		}
		if (exceptionRemoveNonExist) {
			throw new IllegalArgumentException("Gene does not exist!");
		}
		return null;
	}
	public NodeGene removeEqual(NodeGene gene) {
		if (containsEqual(gene)) {
			return remove(gene);
		}
		if (exceptionRemoveNonExist) {
			throw new IllegalArgumentException("Gene does not exist!");
		}
		if (contains(gene)) {
			return nodeMap.get(gene.getInnovationNumber());
		}
		return gene;
	}
	public NodeGene removeExact(NodeGene gene) {
		if (containsExact(gene)) {
			return removeEqual(gene);
		}
		if (exceptionRemoveNonExist) {
			throw new IllegalArgumentException("Gene does not exist!");
		}
		if (contains(gene)) {
			return nodeMap.get(gene.getInnovationNumber());
		}
		return gene;
	}
	
	public boolean contains(NodeGene gene) {
		return innovationSet.contains(gene.getInnovationNumber());
	}
	public boolean contains(long innovationNumber) {
		return innovationSet.contains(innovationNumber);
	}
	public boolean containsEqual(NodeGene gene) {
		if (innovationSet.contains(gene.getInnovationNumber())) {
			NodeGene exactGene = nodeMap.get(gene.getInnovationNumber());
			if (exactGene.isEqual(gene)) {
				return true;
			}
		}
		return false;
	}
	public boolean containsExact(NodeGene gene) {
		//return nodeSet.contains(gene);
		return nodeList.contains(gene);
	}
	
	public NodeGene get(long innovationNumber) {
		if (contains(innovationNumber)) {
			return nodeMap.get(innovationNumber);
		}
		if (exceptionExistsNot) {
			throw new IllegalArgumentException("Gene does not exist!");
		}
		return null;
	}
	public NodeGene get(NodeGene gene) {
		if (contains(gene)) {
			return nodeMap.get(gene.getInnovationNumber());
		}
		if (exceptionExistsNot) {
			throw new IllegalArgumentException("Gene does not exist!");
		}
		return null;
	}
	public NodeGene getEqual(NodeGene gene) {
		if (containsEqual(gene)) {
			return get(gene);
		}
		if (exceptionExistsNot) {
			throw new IllegalArgumentException("Gene does not exist!");
		}
		return null;
	}
	
	public NodeGene set(NodeGene gene) {
		NodeGene previous = gene;
		if (contains(gene)) {
			previous = get(gene);
			nodeList.set(nodeList.indexOf(previous), gene);
			nodeMap.put(gene.getInnovationNumber(), gene);
			nodesByX.get(gene.x).set(nodesByX.get(gene.x).indexOf(previous), gene);
		}
		else {
			add(gene);
		}
		return previous;
	}
	
	public boolean contains(RepresentativeNodeGene gene) {
		return contains(gene.innovationNumber);
	}
	public NodeGene remove(RepresentativeNodeGene gene) {
		return remove(gene.innovationNumber);
	}
	public NodeGene get(RepresentativeNodeGene gene) {
		return get(gene.innovationNumber);
	}
	
	public Set<Long> getInnovationSet() {
		return innovationSet;
	}
	public List<NodeGene> getList() {
		return nodeList;
	}
	public Map<Long, NodeGene> getMap() {
		return nodeMap;
	}
	/*public Set<NodeGene> getSet() {
		return nodeSet;
	}*/
	
	public Map<Float, List<NodeGene>> getNodesByX() {
		return nodesByX;
	}

	public int size() {
		return nodeList.size();
	}
	
	public NodeGene getRandom() {
		int index = (int) Math.floor(Math.random()*size());
		return nodeList.get(index);
	}
	
	public NodeList copy(Genome genome) {
		NodeList list = new NodeList();
		list.exceptionAlreadyExists = exceptionAlreadyExists;
		list.exceptionExistsNot = exceptionExistsNot;
		list.exceptionRemoveNonExist = exceptionRemoveNonExist;
		for (NodeGene gene : nodeList) {
			list.add(gene.copy(genome));
		}
		return list;
	}
	
	public NodeList copyExact() {
		NodeList list = new NodeList();
		list.exceptionAlreadyExists = exceptionAlreadyExists;
		list.exceptionExistsNot = exceptionExistsNot;
		list.exceptionRemoveNonExist = exceptionRemoveNonExist;
		for (NodeGene gene : nodeList) {
			list.add(gene);
		}
		return list;
	}
	
	private class Itr implements Iterator<Entry<Long, NodeGene>> {
		
		Iterator<NodeGene> iterator;
		
		public Itr() {
			iterator = nodeList.iterator();
		}
		
		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}
		
		@Override
		public Entry<Long, NodeGene> next() {
			NodeGene gene = iterator.next();
			return new Entry(gene.getInnovationNumber(), gene);
		}
		
	}
	
	@Override
	public Iterator<Entry<Long, NodeGene>> iterator() {
		return new Itr();
	}
	
	@Override
	public String toString() {
		String string = "";
		Iterator<Entry<Long, NodeGene>> iterator = iterator();
		while (iterator.hasNext()) {
			Entry<Long, NodeGene> entry = iterator.next();
			string += entry.getValue();
			if (iterator.hasNext()) {
				string += "\n";
			}
		}
		return string;
	}
	
	public String toStringX() {
		String string = "\n";
		for (java.util.Map.Entry<Float, List<NodeGene>> layerEntry : nodesByX.entrySet()) {
			string += layerEntry.getKey();
			string += " - ";
			for (NodeGene node : layerEntry.getValue()) {
				string += node;
				string += ", ";
			}
			string += "\n";
		}
		return string;
	}
	
}
