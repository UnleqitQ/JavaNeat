package org.neat.activationFunction;

import org.neat.neat.Neat;

public class ActivationGauss extends ActivationFunction {

	public ActivationGauss(Neat neat) {
		super(neat);
	}

	@Override
	public float calculate(float input) {
		return (float) Math.exp(-5*Math.pow(input, 2));
	}

	@Override
	public ActivationFunction createNew() {
		return new ActivationGauss(neat);
	}

}
