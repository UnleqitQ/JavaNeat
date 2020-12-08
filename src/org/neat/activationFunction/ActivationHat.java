package org.neat.activationFunction;

public class ActivationHat implements ActivationFunction {
	
	public float a = 1;
	
	public float calculate(float input) {
		return Math.max(a-Math.abs(input), 0);
	}
	
}
