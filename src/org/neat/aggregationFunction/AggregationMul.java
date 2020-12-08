package org.neat.aggregationFunction;

public class AggregationMul implements AggregationFunction {

	@Override
	public float calculate(Object... values) {
		float mul = 1;
		for (Object object : values) {
			mul *= (float) object;
		}
		return mul;
	}
	
	@Override
	public float connectionValDisabled() {
		return 1;
	}
	@Override
	public float nodeValDisabled() {
		return 1;
	}
	
}
