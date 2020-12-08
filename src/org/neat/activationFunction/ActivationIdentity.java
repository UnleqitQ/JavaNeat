package org.neat.activationFunction;

public class ActivationIdentity implements ActivationFunction {

	@Override
	public float calculate(float input) {
		return input;
	}

}
