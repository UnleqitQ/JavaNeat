package org.neat.activationFunction;

import java.util.ArrayList;
import java.util.List;

public class ActivationParameter {
	
	public float minimum = -1;
	public float maximum = 1;
	public float value = 1;
	public String name = "Parameter";
	public boolean hasBounds = true;
	public boolean canMutate = true;
	List<Float> disallowed = new ArrayList<>();
	
	public ActivationParameter(float value) {
		this.value = value;
	}
	public ActivationParameter(String name, float value) {
		this.name = name;
		this.value = value;
	}
	
	public void mutate() {
		mutate(1);
	}
	public void mutate(float difference) {
		if (canMutate) {
			float prev = value;
			value += difference*(Math.random()*2-1);
			if (hasBounds) {
				if (value<minimum) {
					value = minimum;
				}
				if (value>maximum) {
					value = maximum;
				}
			}
			if (disallowed.contains(value)) {
				value = prev;
			}
		}
	}
	
	@Override
	public String toString() {
		String string = "";
		string += name;
		string += ": ";
		string += value;
		return string;
	}
	
	public ActivationParameter copy() {
		ActivationParameter copy = new ActivationParameter(name, value);
		copy.minimum = this.minimum;
		copy.maximum = this.maximum;
		copy.hasBounds = this.hasBounds;
		copy.canMutate = this.canMutate;
		for (Float val : disallowed) {
			copy.disallowed.add(val);
		}
		return copy;
	}
	
}
