package org.neat.activationFunction;

import org.neat.neat.Neat;

public class ActivationSiLU extends ActivationFunction {

	public ActivationSiLU(Neat neat) {
		super(neat);
	}

	@Override
	public float calculate(float input) {
		return input*(float) (1/(1+Math.exp(-input)));
	}
	
	@Override
	public ActivationFunction createNew() {
		return new ActivationSiLU(neat);
	}

}
