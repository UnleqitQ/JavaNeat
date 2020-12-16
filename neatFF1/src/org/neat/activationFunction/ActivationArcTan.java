package org.neat.activationFunction;

public class ActivationArcTan implements ActivationFunction {
	
	@Override
	public float calculate(float input) {
		return (float) (Math.atan(input)/Math.PI*2);
	}
	
}
