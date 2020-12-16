package org.neat.activationFunction;

public class ActivationReLU implements ActivationFunction {

	@Override
	public float calculate(float input) {
		if (input>0) {
			return input;
		}
		else {
			return 0;
		}
	}

}
