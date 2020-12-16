package org.neat.aggregationFunction;

import org.neat.genome.Genome;
import org.neat.species.Species;

public class AggregationMax implements AggregationFunction {

	@Override
	public float calculate(Object... values) {
		if (values.length == 0) {
			return 0;
		}
		float max = (float) values[0];
		for (Object object : values) {
			if (((float)object)>max) {
				max = (float) object;
			}
		}
		return max;
	}

}
