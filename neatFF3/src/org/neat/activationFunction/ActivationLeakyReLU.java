package org.neat.activationFunction;

import org.neat.neat.Neat;

public class ActivationLeakyReLU extends ActivationFunction {
	
	public ActivationParameter a;
	
	public ActivationLeakyReLU(Neat neat) {
		super(neat);
		a = new ActivationParameter("a", 0.01f);
		a.minimum = 0.001f;
		a.maximum = 0.1f;
		parameters.put("a", a);
	}
	
	@Override
	public float calculate(float input) {
		if (input>0) {
			return input;
		}
		else {
			return input*a.value;
		}
	}
	
	@Override
	public ActivationFunction createNew() {
		return new ActivationLeakyReLU(neat);
	}
	
	@Override
	public ActivationFunction copy() {
		ActivationLeakyReLU copy = (ActivationLeakyReLU) super.copy();
		copy.a = copy.getParameter("a");
		return copy;
	}

}
