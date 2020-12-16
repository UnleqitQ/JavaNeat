package org.neat.activationFunction;

public class ActivationSiLU implements ActivationFunction {

	@Override
	public float calculate(float input) {
		return input*(float) (1/(1+Math.exp(-input)));
	}

}
