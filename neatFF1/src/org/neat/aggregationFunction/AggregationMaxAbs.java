package org.neat.aggregationFunction;

public class AggregationMaxAbs implements AggregationFunction {

	@Override
	public float calculate(Object... values) {
		float max = 0;
		for (Object object : values) {
			if (Math.abs((float) object) > max) {
				max = Math.abs((float) object);
			}
		}
		return max;
	}

}
