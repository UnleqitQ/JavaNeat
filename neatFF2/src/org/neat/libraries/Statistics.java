package org.neat.libraries;

import java.util.*;
import java.util.Map.Entry;

public class Statistics {
	
	//public List<Float> elementsList = new ArrayList<>();
	public Map<Double, Double> elements = new HashMap<>();
	
	public void add(double value) {
		add(value, 1);
	}
	
	public void add(double value, double amount) {
		if (amount+getAmount(value)>=0) {
			elements.put(value, amount+getAmount(value));
		}
	}
	public void put(double value, double amount) {
		if (amount>=0) {
			elements.put(value, amount);
		}
	}
	
	public boolean contains(double value) {
		return elements.containsKey(value);
	}
	
	public double getAmount(double value) {
		if (contains(value)) {
			return elements.get(value);
		}
		else {
			return 0;
		}
	}
	
	public Set<Double> getValues() {
		return elements.keySet();
	}
	
	
	
	public double mean() {
		double sum = 0;
		double amount = 0;
		for (Entry<Double, Double> entry : elements.entrySet()) {
			sum += entry.getKey()*entry.getValue();
			amount += entry.getValue();
		}
		if (amount == 0) {
			return 0;
		}
		else {
			return sum/amount;
		}
	}
	/*public double geometricMean() {
		double prod = 1;
		double amount = 0;
		for (double value : elements.keySet()) {
			if (getAmount(value)!=0) {
				prod *= Math.pow(value, elements.get(value));
				amount += elements.get(value);
			}
		}
		if (amount == 0) {
			return 1;
		}
		else {
			return Math.pow(prod, 1/amount);
		}
	}
	public double harmonicMean() {
		double sumTop = 0;
		double sumBottom = 0;
		for (double value : elements.keySet()) {
			sumTop += getAmount(value);
			sumBottom += getAmount(value)/value;
		}
		return sumTop/sumBottom;
	}*/
	
	public double modal() {
		Object[] sorted = elements.keySet().toArray();
		Arrays.sort(sorted, new Comparator<Object>() {
			@Override
			public int compare(Object arg0, Object arg1) {
				return Double.compare(getAmount((double) arg0), getAmount((double) arg0));
			}
		}.reversed());
		return (double) sorted[0];
	}
	
	public double median() {
		Object[] sorted = elements.keySet().toArray();
		Arrays.sort(sorted, new Comparator<Object>() {
			@Override
			public int compare(Object arg0, Object arg1) {
				return Double.compare((double) arg0, (double) arg0);
			}
		});
		double sum = 0;
		for (double amount : elements.values()) {
			sum += amount;
		}
		double curr = 0;
		for (int k = 0; k < sorted.length; k++) {
			curr += getAmount((double) sorted[k]);
			if (curr>sum/2) {
				return (double) sorted[k];
			}
			if (curr==sum/2) {
				return (((double) sorted[k])+((double) sorted[k+1]))/2;
			}
		}
		return 0;
	}
	
	public double range() {
		Object[] sorted = elements.keySet().toArray();
		Arrays.sort(sorted, new Comparator<Object>() {
			@Override
			public int compare(Object arg0, Object arg1) {
				return Double.compare((double) arg0, (double) arg0);
			}
		});
		return ((double) sorted[0])-((double) sorted[sorted.length-1]);
	}
	
	public double meanLinearDeviation() {
		double mean = mean();
		double sum = 0;
		double amount = 0;
		for (Entry<Double, Double> entry : elements.entrySet()) {
			sum += entry.getValue() * Math.abs(entry.getKey()-mean);
			amount += entry.getValue();
		}
		return sum/amount;
	}
	
	public double variance() {
		double mean = mean();
		double sum = 0;
		double amount = 0;
		for (Entry<Double, Double> entry : elements.entrySet()) {
			sum += entry.getValue() * Math.pow(entry.getKey()-mean, 2);
			amount += entry.getValue();
		}
		return sum/(amount-1);
	}
	
	public double standardDeviation() {
		return Math.pow(variance(), 1/2);
	}
	
	public double max() {
		Object[] sorted = elements.keySet().toArray();
		Arrays.sort(sorted, new Comparator<Object>() {
			@Override
			public int compare(Object arg0, Object arg1) {
				return Double.compare((double) arg0, (double) arg0);
			}
		});
		return (double) sorted[sorted.length-1];
	}
	public double min() {
		Object[] sorted = elements.keySet().toArray();
		Arrays.sort(sorted, new Comparator<Object>() {
			@Override
			public int compare(Object arg0, Object arg1) {
				return Double.compare((double) arg0, (double) arg0);
			}
		});
		return (double) sorted[sorted.length-1];
	}
	
	
	
	
	private int binomialCoefficient(int n, int k) {
		return faculty(n)/(faculty(k)*faculty(n-k));
	}
	
	private int faculty(int n) {
		if (n<0) {
			return 0;
		}
		if (n==0) {
			return 1;
		}
		return faculty(n-1)*n;
	}
	
}
