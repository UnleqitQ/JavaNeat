package org.neat.activationFunction;

public class ActivationSoftplus implements ActivationFunction {
	
	public float k = 1;
	
	@Override
	public float calculate(float input) {
		return (float) Math.log(1+Math.exp(k*input))/k;
	}

}
