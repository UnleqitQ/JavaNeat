package org.neat.activationFunction;

import org.neat.neat.Neat;

public class ActivationIdentity extends ActivationFunction {

	public ActivationIdentity(Neat neat) {
		super(neat);
	}

	@Override
	public float calculate(float input) {
		return input;
	}

	@Override
	public ActivationFunction createNew() {
		return new ActivationIdentity(neat);
	}

}
