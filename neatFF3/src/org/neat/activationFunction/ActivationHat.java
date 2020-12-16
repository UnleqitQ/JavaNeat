package org.neat.activationFunction;

import org.neat.neat.Neat;

public class ActivationHat extends ActivationFunction {
	
	public ActivationParameter a;
	
	public ActivationHat(Neat neat) {
		super(neat);
		a = new ActivationParameter("a", 1);
		a.minimum = 0.1f;
		a.maximum = 2;
		parameters.put("a", a);
	}
	
	public float calculate(float input) {
		return Math.max(a.value-Math.abs(input), 0);
	}

	@Override
	public ActivationFunction createNew() {
		return new ActivationHat(neat);
	}
	
	@Override
	public ActivationFunction copy() {
		ActivationHat copy = (ActivationHat) super.copy();
		copy.a = copy.getParameter("a");
		return copy;
	}
	
}
