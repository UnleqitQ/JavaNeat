package org.neat.activationFunction;

public class ActivationExp implements ActivationFunction {

	@Override
	public float calculate(float input) {
		return (float) Math.exp(input);
	}

}
