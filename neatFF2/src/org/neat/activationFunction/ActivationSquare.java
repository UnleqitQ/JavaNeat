package org.neat.activationFunction;

public class ActivationSquare implements ActivationFunction {

	@Override
	public float calculate(float input) {
		return input*input;
	}

}
