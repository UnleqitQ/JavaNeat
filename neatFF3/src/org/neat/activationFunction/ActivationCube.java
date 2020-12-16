package org.neat.activationFunction;

import org.neat.neat.Neat;

public class ActivationCube extends ActivationFunction {

	public ActivationCube(Neat neat) {
		super(neat);
	}

	@Override
	public float calculate(float input) {
		return input*input*input;
	}

	@Override
	public ActivationFunction createNew() {
		return new ActivationCube(neat);
	}

}
