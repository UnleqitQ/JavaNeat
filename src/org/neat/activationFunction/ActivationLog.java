package org.neat.activationFunction;

public class ActivationLog implements ActivationFunction {

	@Override
	public float calculate(float input) {
		if (input>0) {
			return (float) Math.log(input);
		}
		else {
			return 0;
		}
	}

}
