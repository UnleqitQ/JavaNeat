package org.neat.activationFunction;

import org.neat.neat.Neat;

public class ActivationAbs extends ActivationFunction {

	public ActivationAbs(Neat neat) {
		super(neat);
	}

	@Override
	public float calculate(float input) {
		return Math.abs(input);
	}

	@Override
	public ActivationFunction createNew() {
		return new ActivationAbs(neat);
	}

}
