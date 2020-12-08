package org.neat.gene;

import java.util.*;

import org.neat.genome.*;
import org.neat.libraries.*;
import org.neat.neat.*;

public class ConnectionList implements Iterable<Entry<Long, ConnectionGene>> {
	
	final Neat neat;
	
	//Set<ConnectionGene> connectionSet = new HashSet();
	Set<Long> innovationSet = new HashSet();
	List<ConnectionGene> connectionList = new ArrayList();
	Map<Long, ConnectionGene> connectionMap = new HashMap();
	public boolean exceptionAlreadyExists = false;
	public boolean exceptionRemoveNonExist = false;
	public boolean exceptionExistsNot = true;
	
	public ConnectionList(Neat neat) {
		this.neat = neat;
	}
	
	public void clear() {
		innovationSet.clear();
		connectionList.clear();
		connectionMap.clear();
	}
	
	public void add(ConnectionList other) {
		boolean bef = exceptionAlreadyExists;
		exceptionAlreadyExists = false;
		for (ConnectionGene connection : other.getList()) {
			add(connection);
		}
		exceptionAlreadyExists = bef;
	}
	public void remove(ConnectionList other) {
		boolean bef = exceptionRemoveNonExist;
		exceptionRemoveNonExist = false;
		for (ConnectionGene connection : other.getList()) {
			remove(connection);
		}
		exceptionRemoveNonExist = bef;
	}
	
	public ConnectionGene add(ConnectionGene gene) {
		if (innovationSet.contains(gene.getInnovationNumber())) {
			if (exceptionAlreadyExists) {
				throw new IllegalArgumentException("Gene already exists!");
			}
			return gene;
		}
		//connectionSet.add(gene);
		innovationSet.add(gene.getInnovationNumber());
		connectionList.add(gene);
		connectionMap.put(gene.getInnovationNumber(), gene);
		return gene;
	}
	
	public ConnectionGene remove(ConnectionGene gene) {
		if (contains(gene)) {
			innovationSet.remove(gene.getInnovationNumber());
			ConnectionGene exactGene = connectionMap.remove(gene.getInnovationNumber());
			connectionList.remove(exactGene);
			//connectionSet.remove(exactGene);
			return exactGene;
		}
		if (exceptionRemoveNonExist) {
			throw new IllegalArgumentException("Gene does not exist!");
		}
		return gene;
	}
	public ConnectionGene remove(long innovationNumber) {
		if (contains(innovationNumber)) {
			innovationSet.remove(innovationNumber);
			ConnectionGene exactGene = connectionMap.remove(innovationNumber);
			connectionList.remove(exactGene);
			//connectionSet.remove(exactGene);
			return exactGene;
		}
		if (exceptionRemoveNonExist) {
			throw new IllegalArgumentException("Gene does not exist!");
		}
		return null;
	}
	public ConnectionGene removeEqual(ConnectionGene gene) {
		if (containsEqual(gene)) {
			return remove(gene);
		}
		if (exceptionRemoveNonExist) {
			throw new IllegalArgumentException("Gene does not exist!");
		}
		if (contains(gene)) {
			return connectionMap.get(gene.getInnovationNumber());
		}
		return gene;
	}
	public ConnectionGene removeEqualExactlier(ConnectionGene gene) {
		if (containsEqualExactlier(gene)) {
			return remove(gene);
		}
		if (exceptionRemoveNonExist) {
			throw new IllegalArgumentException("Gene does not exist!");
		}
		if (contains(gene)) {
			return connectionMap.get(gene.getInnovationNumber());
		}
		return gene;
	}
	public ConnectionGene removeEqualExactliest(ConnectionGene gene) {
		if (containsEqualExactliest(gene)) {
			return remove(gene);
		}
		if (exceptionRemoveNonExist) {
			throw new IllegalArgumentException("Gene does not exist!");
		}
		if (contains(gene)) {
			return connectionMap.get(gene.getInnovationNumber());
		}
		return gene;
	}
	public ConnectionGene removeExact(ConnectionGene gene) {
		if (containsExact(gene)) {
			return removeEqual(gene);
		}
		if (exceptionRemoveNonExist) {
			throw new IllegalArgumentException("Gene does not exist!");
		}
		if (contains(gene)) {
			return connectionMap.get(gene.getInnovationNumber());
		}
		return gene;
	}
	
	public boolean contains(ConnectionGene gene) {
		return innovationSet.contains(gene.getInnovationNumber());
	}
	public boolean contains(long innovationNumber) {
		return innovationSet.contains(innovationNumber);
	}
	public boolean containsEqual(ConnectionGene gene) {
		if (innovationSet.contains(gene.getInnovationNumber())) {
			ConnectionGene exactGene = connectionMap.get(gene.getInnovationNumber());
			if (exactGene.isEqual(gene)) {
				return true;
			}
		}
		return false;
	}
	public boolean containsEqualExactlier(ConnectionGene gene) {
		if (innovationSet.contains(gene.getInnovationNumber())) {
			ConnectionGene exactGene = connectionMap.get(gene.getInnovationNumber());
			if (exactGene.isEqualExactlier(gene)) {
				return true;
			}
		}
		return false;
	}
	public boolean containsEqualExactliest(ConnectionGene gene) {
		if (innovationSet.contains(gene.getInnovationNumber())) {
			ConnectionGene exactGene = connectionMap.get(gene.getInnovationNumber());
			if (exactGene.isEqualExactliest(gene)) {
				return true;
			}
		}
		return false;
	}
	public boolean containsExact(ConnectionGene gene) {
		//return connectionSet.contains(gene);
		return connectionList.contains(gene);
	}
	
	public ConnectionGene get(long innovationNumber) {
		if (contains(innovationNumber)) {
			return connectionMap.get(innovationNumber);
		}
		if (exceptionExistsNot) {
			throw new IllegalArgumentException("Gene does not exist!");
		}
		return null;
	}
	public ConnectionGene get(ConnectionGene gene) {
		if (contains(gene)) {
			return connectionMap.get(gene.getInnovationNumber());
		}
		if (exceptionExistsNot) {
			throw new IllegalArgumentException("Gene does not exist!");
		}
		return null;
	}
	public ConnectionGene getEqual(ConnectionGene gene) {
		if (containsEqual(gene)) {
			return get(gene);
		}
		if (exceptionExistsNot) {
			throw new IllegalArgumentException("Gene does not exist!");
		}
		return null;
	}
	
	public ConnectionGene set(ConnectionGene gene) {
		ConnectionGene previous = gene;
		if (contains(gene)) {
			previous = remove(gene);
		}
		add(gene);
		return previous;
	}
	
	public boolean contains(RepresentativeConnectionGene gene) {
		return contains(gene.innovationNumber);
	}
	public ConnectionGene remove(RepresentativeConnectionGene gene) {
		return remove(gene.innovationNumber);
	}
	public ConnectionGene get(RepresentativeConnectionGene gene) {
		return get(gene.innovationNumber);
	}
	
	public ConnectionGene removeRandom() {
		int index = (int) Math.floor(Math.random()*size());
		return remove(connectionList.get(index));
	}
	public ConnectionGene getRandom() {
		int index = (int) Math.floor(Math.random()*size());
		return connectionList.get(index);
	}
	
	public Set<Long> getInnovationSet() {
		return innovationSet;
	}
	public List<ConnectionGene> getList() {
		return connectionList;
	}
	public Map<Long, ConnectionGene> getMap() {
		return connectionMap;
	}
	/*public Set<ConnectionGene> getSet() {
		return connectionSet;
	}*/
	
	public ConnectionGene get(NodeGene from, NodeGene to) {
		return get(from.getInnovationNumber()+to.getInnovationNumber()*neat.maxNodes);
	}
	public ConnectionGene get(RepresentativeNodeGene from, RepresentativeConnectionGene to) {
		return get(from.innovationNumber+to.innovationNumber*neat.maxNodes);
	}
	public ConnectionGene get(int from, int to) {
		return get(from+to*neat.maxNodes);
	}
	public boolean contains(NodeGene from, NodeGene to) {
		return contains(from.getInnovationNumber()+to.getInnovationNumber()*neat.maxNodes);
	}
	public boolean contains(RepresentativeNodeGene from, RepresentativeConnectionGene to) {
		return contains(from.innovationNumber+to.innovationNumber*neat.maxNodes);
	}
	public boolean contains(int from, int to) {
		return contains(from+to*neat.maxNodes);
	}

	public int size() {
		return connectionList.size();
	}
	
	public ConnectionList copy(Genome genome, NodeList nodeList) {
		ConnectionList list = new ConnectionList(neat);
		list.exceptionAlreadyExists = exceptionAlreadyExists;
		list.exceptionExistsNot = exceptionExistsNot;
		list.exceptionRemoveNonExist = exceptionRemoveNonExist;
		for (ConnectionGene gene : connectionList) {
			list.add(gene.copy(genome, nodeList.get(gene.fromGene), nodeList.get(gene.toGene)));
		}
		return list;
	}
	
	private class Itr implements Iterator<Entry<Long, ConnectionGene>> {
		
		Iterator<ConnectionGene> iterator;
		
		public Itr() {
			iterator = connectionList.iterator();
		}
		
		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}
		
		@Override
		public Entry<Long, ConnectionGene> next() {
			ConnectionGene gene = iterator.next();
			return new Entry(gene.getInnovationNumber(), gene);
		}
		
	}
	
	@Override
	public Iterator<Entry<Long, ConnectionGene>> iterator() {
		return new Itr();
	}
	
	@Override
	public String toString() {
		String string = "";
		Iterator<Entry<Long, ConnectionGene>> iterator = iterator();
		while (iterator.hasNext()) {
			Entry<Long, ConnectionGene> entry = iterator.next();
			string += entry.getValue();
			if (iterator.hasNext()) {
				string += "\n";
			}
		}
		return string;
	}
	
}
