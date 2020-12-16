package org.neat.aggregationFunction;

import org.neat.genome.Genome;
import org.neat.species.Species;

public class AggregationMean implements AggregationFunction {

	@Override
	public float calculate(Object... values) {
		if (values.length == 0) {
			return 0;
		}
		float sum = 0;
		for (Object object : values) {
			sum += (float) object;
		}
		return sum/((float) values.length);
	}

}
