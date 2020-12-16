package org.neat.activationFunction;

import org.neat.neat.Neat;

public class ActivationSoftplus extends ActivationFunction {
	
	public ActivationParameter k;
	
	public ActivationSoftplus(Neat neat) {
		super(neat);
		k = new ActivationParameter("k", 1);
		k.minimum = 0.01f;
		k.maximum = 5;
		k.disallowed.add(0f);
		parameters.put("k", k);
	}
	
	@Override
	public float calculate(float input) {
		return (float) Math.log(1+Math.exp(k.value*input))/k.value;
	}
	
	@Override
	public ActivationFunction createNew() {
		return new ActivationSoftplus(neat);
	}
	
	@Override
	public ActivationFunction copy() {
		ActivationSoftplus copy = (ActivationSoftplus) super.copy();
		copy.k = copy.getParameter("k");
		return copy;
	}

}
