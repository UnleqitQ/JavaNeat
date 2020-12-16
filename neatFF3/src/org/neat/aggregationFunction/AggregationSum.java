package org.neat.aggregationFunction;

public class AggregationSum implements AggregationFunction {
	
	@Override
	public float calculate(Object... inputs) {
		float sum = 0;
		for (Object f : inputs) {
			sum += (float) f;
		}
		return sum;
	}
	
}
