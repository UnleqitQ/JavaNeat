package org.neat.activationFunction;

import org.neat.neat.Neat;

public class ActivationSigmoid extends ActivationFunction {
	
	public ActivationParameter L;
	public ActivationParameter k;
	public ActivationParameter x0;
	
	public ActivationSigmoid(Neat neat) {
		super(neat);
		L = new ActivationParameter("L", 1);
		L.minimum = -2;
		L.maximum = 2;
		parameters.put("L", L);
		k = new ActivationParameter("k", 1);
		k.minimum = -2;
		k.maximum = 2;
		parameters.put("k", k);
		x0 = new ActivationParameter("x0", 0);
		x0.minimum = -2;
		x0.maximum = 2;
		parameters.put("x0", x0);
	}
	
	@Override
	public float calculate(float input) {
		return (float) (L.value/(1+Math.exp(-k.value*(input-x0.value))));
	}
	
	@Override
	public ActivationFunction createNew() {
		return new ActivationSigmoid(neat);
	}
	
	@Override
	public ActivationFunction copy() {
		ActivationSigmoid copy = (ActivationSigmoid) super.copy();
		copy.k = copy.getParameter("k");
		copy.L = copy.getParameter("L");
		copy.x0 = copy.getParameter("x0");
		return copy;
	}
	
}
