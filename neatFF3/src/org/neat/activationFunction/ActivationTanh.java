package org.neat.activationFunction;

import org.neat.neat.Neat;

public class ActivationTanh extends ActivationFunction {

	public ActivationTanh(Neat neat) {
		super(neat);
	}

	@Override
	public float calculate(float input) {
		return (float) Math.tanh(input);
	}
	
	@Override
	public ActivationFunction createNew() {
		return new ActivationTanh(neat);
	}

}
