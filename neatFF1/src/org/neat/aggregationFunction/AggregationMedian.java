package org.neat.aggregationFunction;

import org.neat.mathematics.Statistics;

public class AggregationMedian implements AggregationFunction {

	@Override
	public float calculate(Object... values) {
		Statistics statistics = new Statistics();
		for (Object object : values) {
			statistics.add((double) object);
		}
		return (float) statistics.median();
	}

}
