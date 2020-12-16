package org.neat.activationFunction;

import org.neat.neat.Neat;

public class ActivationArcTan extends ActivationFunction {
	
	public ActivationArcTan(Neat neat) {
		super(neat);
	}

	@Override
	public float calculate(float input) {
		return (float) (Math.atan(input)/Math.PI*2);
	}

	@Override
	public ActivationFunction createNew() {
		return new ActivationArcTan(neat);
	}
	
}
