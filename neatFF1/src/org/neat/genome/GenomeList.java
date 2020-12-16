package org.neat.genome;

import java.util.*;

import org.neat.gene.NodeGene;
import org.neat.libraries.*;

public class GenomeList implements Iterable<Entry<Integer, Genome>> {
	
	Set<Integer> ids = new HashSet<>();
	List<Genome> genomeList = new ArrayList<>();
	Map<Integer, Genome> genomeMap = new HashMap<>();
	public int nextId = 0;
	
	public void add(Genome genome) {
		ids.add(genome.id);
		genomeList.add(genome);
		genomeMap.put(genome.id, genome);
		nextId++;
	}
	
	public Genome remove(int ID) {
		if (ids.contains(ID)) {
			Genome genome = genomeMap.remove(ID);
			ids.remove(ID);
			genomeList.remove(genome);
			return genome;
		}
		return null;
	}
	public Genome remove(Genome genome) {
		if (ids.contains(genome.id)) {
			Genome exactGenome = genomeMap.remove(genome.id);
			ids.remove(genome.id);
			genomeList.remove(exactGenome);
			return exactGenome;
		}
		return genome;
	}
	public void removeExact(Genome genome) {
		if (genomeList.contains(genome)) {
			genomeMap.remove(genome);
			genomeList.remove(genome);
			ids.remove(genome.id);
		}
	}
	
	public Genome get(int ID) {
		if (ids.contains(ID)) {
			return genomeMap.get(ID);
		}
		return null;
	}
	public Genome get(Genome genome) {
		if (ids.contains(genome.id)) {
			return genomeMap.get(genome.id);
		}
		return null;
	}
	
	public boolean contains(Genome genome) {
		return ids.contains(genome.id);
	}
	public boolean containsExactly(Genome genome) {
		return genomeList.contains(genome);
	}
	
	
	public int size() {
		return genomeList.size();
	}
	
	public List<Genome> getList() {
		return genomeList;
	}
	public Map<Integer, Genome> getMap() {
		return genomeMap;
	}
	public Set<Integer> getIDs() {
		return ids;
	}
	
	private class Itr implements Iterator<Entry<Integer, Genome>> {
		
		Iterator<Genome> iterator;
		
		public Itr() {
			iterator = genomeList.iterator();
		}
		
		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}
		
		@Override
		public Entry<Integer, Genome> next() {
			Genome genome = iterator.next();
			return new Entry(genome.id, genome);
		}
	}
	
	@Override
	public Iterator<Entry<Integer, Genome>> iterator() {
		return new Itr();
	}
	
	public Genome getRandom() {
		return genomeList.get((int)Math.floor(genomeList.size()*Math.random()));
	}
	
	public Genome removeRandom() {
		return remove(getRandom());
	}

	public void add(List<Genome> genomes) {
		for (Genome genome : genomes) {
			this.add(genome);
		}
	}

	public void remove(List<Genome> cutGenomes) {
		for (Genome genome : cutGenomes) {
			remove(genome);
		}
	}
	
	public void clear() {
		genomeList.clear();
		genomeMap.clear();
		ids.clear();
	}
	
	@Override
	public String toString() {
		String string = "\n";
		Iterator<Entry<Integer, Genome>> iterator = iterator();
		while (iterator.hasNext()) {
			Entry<Integer, Genome> entry = iterator.next();
			string += entry.getValue();
			if (iterator.hasNext()) {
				string += "\n";
			}
		}
		string += "\n";
		return string;
	}
	
	
	
}
