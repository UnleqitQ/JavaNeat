package org.neat.aggregationFunction;

public class AggregationSumSqr implements AggregationFunction {

	@Override
	public float calculate(Object... values) {
		float sum = 0;
		for (Object object : values) {
			sum += ((float) object)*((float) object);
		}
		return sum;
	}
	
}
