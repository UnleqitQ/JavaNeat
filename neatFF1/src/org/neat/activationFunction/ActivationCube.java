package org.neat.activationFunction;

public class ActivationCube implements ActivationFunction {

	@Override
	public float calculate(float input) {
		return input*input*input;
	}

}
