package org.neat.activationFunction;

import org.neat.neat.Neat;

public class ActivationSin extends ActivationFunction {

	public ActivationSin(Neat neat) {
		super(neat);
	}

	@Override
	public float calculate(float input) {
		return (float) Math.sin(Math.max(-1, Math.min(1, input))*Math.PI);
	}
	
	@Override
	public ActivationFunction createNew() {
		return new ActivationSin(neat);
	}

}
