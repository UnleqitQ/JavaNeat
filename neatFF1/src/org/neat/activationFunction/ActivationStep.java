package org.neat.activationFunction;

public class ActivationStep implements ActivationFunction {
	
	@Override
	public float calculate(float input) {
		if (input>0) {
			return 1;
		}
		else {
			return 0;
		}
	}
	
}
