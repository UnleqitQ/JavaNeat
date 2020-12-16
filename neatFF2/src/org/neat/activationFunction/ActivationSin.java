package org.neat.activationFunction;

public class ActivationSin implements ActivationFunction {

	@Override
	public float calculate(float input) {
		return (float) Math.sin(Math.max(-1, Math.min(1, input))*Math.PI);
	}

}
