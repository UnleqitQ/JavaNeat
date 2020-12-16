package org.neat.activationFunction;

public class ActivationSigmoid implements ActivationFunction {
	
	public float L = 1;
	public float k = 1;
	public float x0 = 0;
	
	@Override
	public float calculate(float input) {
		return (float) (L/(1+Math.exp(-k*(input-x0))));
	}
	
}
