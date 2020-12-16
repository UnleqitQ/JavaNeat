package org.neat.activationFunction;

import org.neat.neat.Neat;

public class ActivationClamped extends ActivationFunction {
	
	public ActivationParameter a;
	
	public ActivationClamped(Neat neat) {
		super(neat);
		a = new ActivationParameter("a", 1);
		a.minimum = 0.1f;
		a.maximum = 2;
		parameters.put("a", a);
	}
	
	@Override
	public float calculate(float input) {
		return Math.max(-a.value, Math.min(a.value, input));
	}

	@Override
	public ActivationFunction createNew() {
		return new ActivationClamped(neat);
	}
	
	@Override
	public ActivationFunction copy() {
		ActivationClamped copy = (ActivationClamped) super.copy();
		copy.a = copy.getParameter("a");
		return copy;
	}
	
}
