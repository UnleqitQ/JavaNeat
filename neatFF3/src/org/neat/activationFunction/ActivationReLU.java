package org.neat.activationFunction;

import org.neat.neat.Neat;

public class ActivationReLU extends ActivationFunction {

	public ActivationReLU(Neat neat) {
		super(neat);
	}

	@Override
	public float calculate(float input) {
		if (input>0) {
			return input;
		}
		else {
			return 0;
		}
	}
	
	@Override
	public ActivationFunction createNew() {
		return new ActivationReLU(neat);
	}

}
