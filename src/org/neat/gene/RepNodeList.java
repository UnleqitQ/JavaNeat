package org.neat.gene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neat.libraries.*;

public class RepNodeList implements Iterable<Entry<Long, RepresentativeNodeGene>> {
	
	//Set<RepresentativeNodeGene> nodeSet = new HashSet();
	Set<Long> innovationSet = new HashSet();
	List<RepresentativeNodeGene> nodeList = new ArrayList();
	Map<Long, RepresentativeNodeGene> nodeMap = new HashMap();
	public boolean exceptionAlreadyExists = false;
	public boolean exceptionRemoveNonExist = false;
	public boolean exceptionExistsNot = false;
	private long nextInnovation = 0;
	
	
	public RepresentativeNodeGene add(RepresentativeNodeGene gene) {
		if (innovationSet.contains(gene.innovationNumber)) {
			if (exceptionAlreadyExists) {
				throw new IllegalArgumentException("Gene already exists!");
			}
			return gene;
		}
		//nodeSet.add(gene);
		innovationSet.add(gene.innovationNumber);
		nodeList.add(gene);
		nodeMap.put(gene.innovationNumber, gene);
		nextInnovation++;
		return gene;
	}
	
	public RepresentativeNodeGene remove(RepresentativeNodeGene gene) {
		if (contains(gene)) {
			innovationSet.remove(gene.innovationNumber);
			RepresentativeNodeGene exactGene = nodeMap.remove(gene.innovationNumber);
			nodeList.remove(exactGene);
			//nodeSet.remove(exactGene);
			return exactGene;
		}
		if (exceptionRemoveNonExist) {
			throw new IllegalArgumentException("Gene does not exist!");
		}
		return gene;
	}
	public RepresentativeNodeGene remove(long innovationNumber) {
		if (contains(innovationNumber)) {
			innovationSet.remove(innovationNumber);
			RepresentativeNodeGene exactGene = nodeMap.remove(innovationNumber);
			nodeList.remove(exactGene);
			//nodeSet.remove(exactGene);
			return exactGene;
		}
		if (exceptionRemoveNonExist) {
			throw new IllegalArgumentException("Gene does not exist!");
		}
		return null;
	}
	
	public boolean contains(RepresentativeNodeGene gene) {
		return innovationSet.contains(gene.innovationNumber);
	}
	public boolean contains(long innovationNumber) {
		return innovationSet.contains(innovationNumber);
	}
	public boolean containsExact(RepresentativeNodeGene gene) {
		//return nodeSet.contains(gene);
		return nodeList.contains(gene);
	}
	
	public RepresentativeNodeGene get(long innovationNumber) {
		if (contains(innovationNumber)) {
			return nodeMap.get(innovationNumber);
		}
		if (exceptionExistsNot) {
			throw new IllegalArgumentException("Gene does not exist!");
		}
		return null;
	}
	public RepresentativeNodeGene get(RepresentativeNodeGene gene) {
		if (contains(gene)) {
			return nodeMap.get(gene.innovationNumber);
		}
		if (exceptionExistsNot) {
			throw new IllegalArgumentException("Gene does not exist!");
		}
		return null;
	}
	
	public RepresentativeNodeGene set(RepresentativeNodeGene gene) {
		if (contains(gene)) {
			RepresentativeNodeGene previous = get(gene);
			nodeList.set(nodeList.indexOf(previous), gene);
			nodeMap.put(gene.innovationNumber, gene);
			//nodeSet.remove(previous);
			//nodeSet.add(gene);
			return previous;
		}
		else {
			add(gene);
			return gene;
		}
	}
	
	public RepresentativeNodeGene add(NodeGene gene) {
		return add(gene.getRepresentative());
	}
	public boolean contains(NodeGene gene) {
		return contains(gene.getRepresentative());
	}
	public RepresentativeNodeGene get(NodeGene gene) {
		return get(gene.getRepresentative());
	}
	public RepresentativeNodeGene remove(NodeGene gene) {
		return remove(gene.getRepresentative());
	}
	public RepresentativeNodeGene set(NodeGene gene) {
		return set(gene.getRepresentative());
	}
	
	public Set<Long> getInnovationSet() {
		return innovationSet;
	}
	public List<RepresentativeNodeGene> getNodeList() {
		return nodeList;
	}
	public Map<Long, RepresentativeNodeGene> getNodeMap() {
		return nodeMap;
	}
	/*public Set<RepresentativeNodeGene> getNodeSet() {
		return nodeSet;
	}*/
	
	public long getNextInnovationNumber() {
		return nextInnovation;
	}

	public int size() {
		return nodeList.size();
	}

	public RepresentativeNodeGene getRandom() {
		int index = (int) Math.floor(Math.random()*size());
		return nodeList.get(index);
	}
	
	private class Itr implements Iterator<Entry<Long, RepresentativeNodeGene>> {
		
		Iterator<RepresentativeNodeGene> iterator;
		
		public Itr() {
			iterator = nodeList.iterator();
		}
		
		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}
		
		@Override
		public Entry<Long, RepresentativeNodeGene> next() {
			RepresentativeNodeGene gene = iterator.next();
			return new Entry(gene.innovationNumber, gene);
		}
		
	}
	
	@Override
	public Iterator<Entry<Long, RepresentativeNodeGene>> iterator() {
		return new Itr();
	}
	
	@Override
	public String toString() {
		String string = "";
		Iterator<Entry<Long, RepresentativeNodeGene>> iterator = iterator();
		while (iterator.hasNext()) {
			Entry<Long, RepresentativeNodeGene> entry = iterator.next();
			string += entry.getValue();
			if (iterator.hasNext()) {
				string += "\n";
			}
		}
		return string;
	}
	
}
