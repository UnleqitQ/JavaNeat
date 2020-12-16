package org.neat.calculate;

import java.util.*;

import org.neat.libraries.*;

/**
 * @author Quentin
 *
 */
public class CalcNodeList implements Iterable<Entry<Long, CalcNode>> {
	
	Map<Long, CalcNode> map = new HashMap();
	Set<CalcNode> set = new HashSet();
	
	public void add(CalcNode node) {
		map.put(node.innovationNumber, node);
		set.add(node);
	}
	
	public CalcNode get(long innovationNumber) {
		return map.get(innovationNumber);
	}
	
	
	public Set<CalcNode> getSet() {
		return set;
	}
	public Map<Long, CalcNode> getMap() {
		return map;
	}
	
	private class Itr implements Iterator<Entry<Long, CalcNode>> {
		
		Iterator<CalcNode> iterator;
		
		public Itr() {
			iterator = set.iterator();
		}
		
		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}
		
		@Override
		public Entry<Long, CalcNode> next() {
			CalcNode gene = iterator.next();
			return new Entry(gene.innovationNumber, gene);
		}
		
	}
	
	@Override
	public Iterator<Entry<Long, CalcNode>> iterator() {
		return new Itr();
	}
	
}
