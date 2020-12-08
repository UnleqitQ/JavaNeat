package org.neat.calculate;

import org.neat.activationFunction.*;
import org.neat.aggregationFunction.*;

/**
 * @author Quentin
 *
 */
public class CalcConnection {
	
	public long innovationNumber;
	public CalcNode node;
	public float weight;
	public float value;
	public boolean enabled;
	
	public CalcConnection(long innovationNumber, CalcNode node, float weight, boolean enabled) {
		this.innovationNumber = innovationNumber;
		this.node = node;
		this.weight = weight;
		this.enabled = enabled;
	}
	
	public float calculate() {
		if (!node.enabled) {
			value = node.aggregationFunction.nodeValDisabled();
			if (Float.isNaN(value)) {
				return Float.NaN;
			}
			else {
				value *= weight;
				return value;
			}
		}
		value = node.calculate();
		if (Float.isNaN(value)) {
			return Float.NaN;
		}
		else {
			value *= weight;
			return value;
		}
	}
	
	
}
