package org.neat.activationFunction;

import org.neat.neat.Neat;

public class ActivationExp extends ActivationFunction {

	public ActivationExp(Neat neat) {
		super(neat);
	}

	@Override
	public float calculate(float input) {
		return (float) Math.exp(input);
	}
	
	@Override
	public ActivationFunction createNew() {
		return new ActivationExp(neat);
	}

}
