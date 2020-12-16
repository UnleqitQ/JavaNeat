package org.neat.activationFunction;

public class ActivationClamped implements ActivationFunction {
	
	public float a = 1;
	
	@Override
	public float calculate(float input) {
		return Math.max(-a, Math.min(a, input));
	}
	
}
