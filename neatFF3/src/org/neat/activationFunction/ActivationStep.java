package org.neat.activationFunction;

import org.neat.neat.Neat;

public class ActivationStep extends ActivationFunction {
	
	public ActivationStep(Neat neat) {
		super(neat);
	}

	@Override
	public float calculate(float input) {
		if (input>0) {
			return 1;
		}
		else {
			return 0;
		}
	}
	
	@Override
	public ActivationFunction createNew() {
		return new ActivationStep(neat);
	}
	
}
