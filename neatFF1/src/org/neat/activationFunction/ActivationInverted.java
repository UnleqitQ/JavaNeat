package org.neat.activationFunction;

public class ActivationInverted implements ActivationFunction {

	@Override
	public float calculate(float input) {
		if (input != 0) {
			return 1/input;
		}
		else {
			return 0;
		}
	}

}
