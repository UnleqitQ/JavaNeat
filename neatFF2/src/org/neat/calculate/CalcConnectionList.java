package org.neat.calculate;

import java.util.*;

import org.neat.libraries.*;

/**
 * @author Quentin
 *
 */
public class CalcConnectionList implements Iterable<Entry<Long, CalcConnection>> {
	
	Map<Long, CalcConnection> map = new HashMap();
	Set<CalcConnection> set = new HashSet();
	
	public void add(CalcConnection connection) {
		map.put(connection.innovationNumber, connection);
		set.add(connection);
	}
	
	public CalcConnection get(long innovationNumber) {
		return map.get(innovationNumber);
	}
	
	
	public Set<CalcConnection> getSet() {
		return set;
	}
	public Map<Long, CalcConnection> getMap() {
		return map;
	}
	
	private class Itr implements Iterator<Entry<Long, CalcConnection>> {
		
		Iterator<CalcConnection> iterator;
		
		public Itr() {
			iterator = set.iterator();
		}
		
		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}
		
		@Override
		public Entry<Long, CalcConnection> next() {
			CalcConnection gene = iterator.next();
			return new Entry(gene.innovationNumber, gene);
		}
		
	}
	
	@Override
	public Iterator<Entry<Long, CalcConnection>> iterator() {
		return new Itr();
	}
	
}
