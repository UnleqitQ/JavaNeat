package org.neat.activationFunction;

import org.neat.neat.Neat;

public class ActivationInverted extends ActivationFunction {

	public ActivationInverted(Neat neat) {
		super(neat);
	}

	@Override
	public float calculate(float input) {
		if (input != 0) {
			return 1/input;
		}
		else {
			return 0;
		}
	}
	
	@Override
	public ActivationFunction createNew() {
		return new ActivationInverted(neat);
	}

}
