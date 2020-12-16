package org.neat.activationFunction;

public class ActivationTanh implements ActivationFunction {

	@Override
	public float calculate(float input) {
		return (float) Math.tanh(input);
	}

}
