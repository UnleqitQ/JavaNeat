package org.neat.activationFunction;

public class ActivationAbs implements ActivationFunction {

	@Override
	public float calculate(float input) {
		return Math.abs(input);
	}

}
