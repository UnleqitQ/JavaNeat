package org.neat.aggregationFunction;

import org.neat.activationFunction.ActivationFunction;

public class AggregationMin implements AggregationFunction {

	@Override
	public float calculate(Object... values) {
		if (values.length == 0) {
			return 0;
		}
		float min = (float) values[0];
		for (Object object : values) {
			if (((float)object)>min) {
				min = (float) object;
			}
		}
		return min;
	}

}
