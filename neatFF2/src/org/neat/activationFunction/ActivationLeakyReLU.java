package org.neat.activationFunction;

public class ActivationLeakyReLU implements ActivationFunction {
	
	public float a = 0.01f;
	
	@Override
	public float calculate(float input) {
		if (input>0) {
			return input;
		}
		else {
			return input*a;
		}
	}

}
