package org.neat.activationFunction;

import org.neat.neat.Neat;

public class ActivationELU extends ActivationFunction {
	
	public ActivationParameter a;
	
	public ActivationELU(Neat neat) {
		super(neat);
		a = new ActivationParameter("a", 1);
		a.minimum = 0.1f;
		a.maximum = 2;
		parameters.put("a", a);
	}
	
	@Override
	public float calculate(float input) {
		if (input>0) {
			return input;
		}
		else {
			return (float) (a.value*(Math.exp(input)-1));
		}
	}

	@Override
	public ActivationFunction createNew() {
		return new ActivationELU(neat);
	}
	
	@Override
	public ActivationFunction copy() {
		ActivationELU copy = (ActivationELU) super.copy();
		copy.a = copy.getParameter("a");
		return copy;
	}

}
