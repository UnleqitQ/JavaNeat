package org.neat.activationFunction;

import org.neat.neat.Neat;

public class ActivationLog extends ActivationFunction {

	public ActivationLog(Neat neat) {
		super(neat);
	}

	@Override
	public float calculate(float input) {
		if (input>0) {
			return (float) Math.log(input);
		}
		else {
			return 0;
		}
	}
	
	@Override
	public ActivationFunction createNew() {
		return new ActivationLog(neat);
	}

}
