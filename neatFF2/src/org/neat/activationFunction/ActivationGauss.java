package org.neat.activationFunction;

public class ActivationGauss implements ActivationFunction {

	@Override
	public float calculate(float input) {
		return (float) Math.exp(-5*Math.pow(input, 2));
	}

}
