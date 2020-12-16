package org.neat.activationFunction;

public class ActivationELU implements ActivationFunction {
	
	public float a = 1;
	
	@Override
	public float calculate(float input) {
		if (input>0) {
			return input;
		}
		else {
			return (float) (a*(Math.exp(input)-1));
		}
	}

}
