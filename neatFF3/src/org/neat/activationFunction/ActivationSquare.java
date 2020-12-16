package org.neat.activationFunction;

import org.neat.neat.Neat;

public class ActivationSquare extends ActivationFunction {

	public ActivationSquare(Neat neat) {
		super(neat);
	}

	@Override
	public float calculate(float input) {
		return input*input;
	}
	
	@Override
	public ActivationFunction createNew() {
		return new ActivationSquare(neat);
	}

}
