package org.neat.aggregationFunction;

public class AggregationSumCube implements AggregationFunction {

	@Override
	public float calculate(Object... values) {
		float sum = 0;
		for (Object object : values) {
			sum += ((float) object)*((float) object)*((float) object);
		}
		return sum;
	}

}
