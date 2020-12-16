package org.neat.aggregationFunction;

public class AggregationRange implements AggregationFunction {

	@Override
	public float calculate(Object... values) {
		float max = (float) values[0];
		for (Object object : values) {
			if (((float)object)>max) {
				max = (float) object;
			}
		}
		float min = (float) values[0];
		for (Object object : values) {
			if (((float)object)<min) {
				min = (float) object;
			}
		}
		return max-min;
	}
	
}
