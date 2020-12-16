package org.neat.gene;

import java.util.*;

import org.neat.libraries.*;
import org.neat.neat.*;

public class RepConnectionList implements Iterable<Entry<Long, RepresentativeConnectionGene>> {
	
	Neat neat;
	//Set<RepresentativeConnectionGene> connectionSet = new HashSet();
	Set<Long> innovationSet = new HashSet();
	List<RepresentativeConnectionGene> connectionList = new ArrayList();
	Map<Long, RepresentativeConnectionGene> connectionMap = new HashMap();
	public boolean exceptionAlreadyExists = false;
	public boolean exceptionRemoveNonExist = false;
	public boolean exceptionExistsNot = false;

	public RepConnectionList(Neat neat) {
		this.neat = neat;
	}
	
	public RepresentativeConnectionGene add(RepresentativeConnectionGene gene) {
		if (innovationSet.contains(gene.innovationNumber)) {
			if (exceptionAlreadyExists) {
				throw new IllegalArgumentException("Gene already exists!");
			}
			return gene;
		}
		//connectionSet.add(gene);
		innovationSet.add(gene.innovationNumber);
		connectionList.add(gene);
		connectionMap.put(gene.innovationNumber, gene);
		return gene;
	}
	
	public RepresentativeConnectionGene remove(RepresentativeConnectionGene gene) {
		if (contains(gene)) {
			innovationSet.remove(gene.innovationNumber);
			RepresentativeConnectionGene exactGene = connectionMap.remove(gene.innovationNumber);
			connectionList.remove(exactGene);
			//connectionSet.remove(exactGene);
			return exactGene;
		}
		if (exceptionRemoveNonExist) {
			throw new IllegalArgumentException("Gene does not exist!");
		}
		return gene;
	}
	public RepresentativeConnectionGene remove(long innovationNumber) {
		if (contains(innovationNumber)) {
			innovationSet.remove(innovationNumber);
			RepresentativeConnectionGene exactGene = connectionMap.remove(innovationNumber);
			connectionList.remove(exactGene);
			//connectionSet.remove(exactGene);
			return exactGene;
		}
		if (exceptionRemoveNonExist) {
			throw new IllegalArgumentException("Gene does not exist!");
		}
		return null;
	}
	
	public boolean contains(RepresentativeConnectionGene gene) {
		return innovationSet.contains(gene.innovationNumber);
	}
	public boolean contains(long innovationNumber) {
		return innovationSet.contains(innovationNumber);
	}
	public boolean containsExact(RepresentativeConnectionGene gene) {
		//return connectionSet.contains(gene);
		return connectionList.contains(gene);
	}
	
	public RepresentativeConnectionGene get(long innovationNumber) {
		if (contains(innovationNumber)) {
			return connectionMap.get(innovationNumber);
		}
		if (exceptionExistsNot) {
			throw new IllegalArgumentException("Gene does not exist!");
		}
		return null;
	}
	public RepresentativeConnectionGene get(RepresentativeConnectionGene gene) {
		if (contains(gene)) {
			return connectionMap.get(gene.innovationNumber);
		}
		if (exceptionExistsNot) {
			throw new IllegalArgumentException("Gene does not exist!");
		}
		return null;
	}
	
	public RepresentativeConnectionGene set(RepresentativeConnectionGene gene) {
		RepresentativeConnectionGene previous = gene;
		if (contains(gene)) {
			previous = remove(gene);
		}
		add(gene);
		return previous;
	}
	
	public RepresentativeConnectionGene get(NodeGene from, NodeGene to) {
		return get(from.getInnovationNumber()+to.getInnovationNumber()*neat.maxNodes);
	}
	public RepresentativeConnectionGene get(RepresentativeNodeGene from, RepresentativeConnectionGene to) {
		return get(from.innovationNumber+to.innovationNumber*neat.maxNodes);
	}
	public RepresentativeConnectionGene get(long from, long to) {
		return get(from+to*neat.maxNodes);
	}
	public boolean contains(NodeGene from, NodeGene to) {
		return contains(from.getInnovationNumber()+to.getInnovationNumber()*neat.maxNodes);
	}
	public boolean contains(RepresentativeNodeGene from, RepresentativeConnectionGene to) {
		return contains(from.innovationNumber+to.innovationNumber*neat.maxNodes);
	}
	public boolean contains(long from, long to) {
		return contains(from+to*neat.maxNodes);
	}
	
	public RepresentativeConnectionGene add(ConnectionGene gene) {
		return add(gene.getRepresentative());
	}
	public boolean contains(ConnectionGene gene) {
		return contains(gene.getRepresentative());
	}
	public RepresentativeConnectionGene get(ConnectionGene gene) {
		return get(gene.getRepresentative());
	}
	public RepresentativeConnectionGene remove(ConnectionGene gene) {
		return remove(gene.getRepresentative());
	}
	public RepresentativeConnectionGene set(ConnectionGene gene) {
		return set(gene.getRepresentative());
	}
	
	public Set<Long> getInnovationSet() {
		return innovationSet;
	}
	public List<RepresentativeConnectionGene> getList() {
		return connectionList;
	}
	public Map<Long, RepresentativeConnectionGene> getMap() {
		return connectionMap;
	}
	/*public Set<RepresentativeConnectionGene> getSet() {
		return connectionSet;
	}*/

	public boolean contains(RepresentativeNodeGene from, RepresentativeNodeGene to) {
		return contains(from.innovationNumber+to.innovationNumber*neat.maxNodes);
	}

	public RepresentativeConnectionGene get(RepresentativeNodeGene from, RepresentativeNodeGene to) {
		return get(from.innovationNumber+to.innovationNumber*neat.maxNodes);
	}

	public float size() {
		return connectionList.size();
	}
	
	public RepresentativeConnectionGene removeRandom() {
		int index = (int) Math.floor(Math.random()*size());
		return remove(connectionList.get(index));
	}
	public RepresentativeConnectionGene getRandom() {
		int index = (int) Math.floor(Math.random()*size());
		return connectionList.get(index);
	}
	
	private class Itr implements Iterator<Entry<Long, RepresentativeConnectionGene>> {
		
		Iterator<RepresentativeConnectionGene> iterator;
		
		public Itr() {
			iterator = connectionList.iterator();
		}
		
		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}
		
		@Override
		public Entry<Long, RepresentativeConnectionGene> next() {
			RepresentativeConnectionGene gene = iterator.next();
			return new Entry(gene.innovationNumber, gene);
		}
		
	}
	
	@Override
	public Iterator<Entry<Long, RepresentativeConnectionGene>> iterator() {
		return new Itr();
	}
	
	@Override
	public String toString() {
		String string = "";
		Iterator<Entry<Long, RepresentativeConnectionGene>> iterator = iterator();
		while (iterator.hasNext()) {
			Entry<Long, RepresentativeConnectionGene> entry = iterator.next();
			string += entry.getValue();
			if (iterator.hasNext()) {
				string += "\n";
			}
		}
		return string;
	}
	
}
