package org.neat.species;

import java.util.*;

import org.neat.genome.Genome;
import org.neat.libraries.*;

public class SpeciesList implements Iterable<Entry<Integer, Species>> {
	
	private Map<Integer, Species> speciesMap = new HashMap<>();
	private List<Species> speciesList = new ArrayList<>();
	private Set<Integer> ids = new HashSet<>();
	
	private List<Integer> randIds = new ArrayList<>();
	
	private int nextId = 0;
	
	public void add(Species species) {
		if (!contains(species)) {
			randIds.clear();
			speciesList.add(species);
			speciesMap.put(species.id, species);
			ids.add(species.id);
			nextId++;
		}
	}
	
	public void set(Species species) {
		if (contains(species)) {
			Species previous = speciesMap.get(species.id);
			int index = speciesList.indexOf(previous);
			speciesMap.put(species.id, species);
			speciesList.set(index, species);
		}
		else {
			add(species);
		}
		randIds.clear();
	}
	
	public Species remove(Species species) {
		if (!contains(species)) {
			return species;
		}
		randIds.clear();
		return remove(species.id);
	}
	
	public Species remove(int id) {
		Species species = speciesMap.remove(id);
		speciesList.remove(species);
		ids.remove(id);
		randIds.clear();
		return species;
	}
	
	public Species get(int id) {
		if (contains(id)) {
			return speciesMap.get(id);
		}
		else {
			return null;
		}
	}
	
	public Species get(Species species) {
		return get(species.id);
	}
	
	public boolean contains(int id) {
		return ids.contains(id);
	}
	
	public boolean contains(Species species) {
		return contains(species.id);
	}
	
	public boolean containsExact(Species species) {
		return speciesList.contains(species);
	}
	
	public Set<Integer> getIds() {
		return ids;
	}
	public List<Species> getList() {
		return speciesList;
	}
	public Map<Integer, Species> getSpeciesMap() {
		return speciesMap;
	}
	
	public int size() {
		return speciesList.size();
	}
	
	public Species getRandom() {
		return speciesList.get((int) Math.floor(size()*Math.random()));
	}
	public Species removeRandom() {
		return remove(getRandom());
	}

	private class Itr implements Iterator<Entry<Integer, Species>> {
		
		Iterator<Species> iterator;
		
		public Itr() {
			iterator = speciesList.iterator();
		}
		
		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}
		
		@Override
		public Entry<Integer, Species> next() {
			Species species = iterator.next();
			return new Entry(species.id, species);
		}
		
	}
	
	@Override
	public Iterator<Entry<Integer, Species>> iterator() {
		return new Itr();
	}
	
	public static Comparator<Entry<Integer, Species>> getEntryComparator() {
		return new CmpEntry();
		
	}
	private static class CmpEntry implements Comparator<Entry<Integer, Species>> {
		@Override
		public int compare(Entry<Integer, Species> o1, Entry<Integer, Species> o2) {
			return o1.getValue().compareTo(o2.getValue());
		}
	}
	
	public static Comparator<Species> getComparator() {
		return new Cmp();
		
	}
	private static class Cmp implements Comparator<Species> {
		@Override
		public int compare(Species o1, Species o2) {
			return o1.compareTo(o2);
		}
	}
	
	public int nextId() {
		return nextId;
	}
	
	public boolean containsWithRepresentative(Genome genome) {
		for (Species species : speciesList) {
			if (genome.equals(species.representative)) {
				return true;
			}
		}
		return false;
	}
	
	public Species getRandomNotEmpty() {
		if (randIds.size() < 1) {
			for (Integer id : ids) {
				randIds.add(id);
			}
		}
		Collections.shuffle(randIds);
		for (Integer id : randIds) {
			if (get(id).genomes.size()>0) {
				return get(id);
			}
		}
		return getRandom();
	}
	
	public void shuffle() {
		Collections.shuffle(speciesList);
	}
	public void reverse() {
		Collections.reverse(speciesList);
	}
	
}
