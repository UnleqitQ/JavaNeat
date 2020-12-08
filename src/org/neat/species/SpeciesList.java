package org.neat.species;

import java.util.*;

import org.neat.genome.Genome;
import org.neat.libraries.*;

public class SpeciesList implements Iterable<Entry<Integer, Species>> {
	
	private Map<Integer, Species> speciesMap = new HashMap<>();
	private List<Species> speciesList = new ArrayList<>();
	private Set<Integer> ids = new HashSet<>();
	
	private int nextId = 0;
	
	public void add(Species species) {
		if (!contains(species)) {
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
	}
	
	public Species remove(Species species) {
		if (!contains(species)) {
			return species;
		}
		return remove(species.id);
	}
	
	public Species remove(int id) {
		Species species = speciesMap.remove(id);
		speciesList.remove(species);
		ids.remove(id);
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
	
	
	
}
