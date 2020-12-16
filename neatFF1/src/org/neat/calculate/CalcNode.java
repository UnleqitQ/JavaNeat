package org.neat.calculate;

import java.util.*;

import org.neat.activationFunction.*;
import org.neat.aggregationFunction.*;

/**
 * @author Quentin
 *
 */
public class CalcNode {
	
	public ActivationFunction activationFunction;
	public AggregationFunction aggregationFunction;
	public boolean enabled;
	public long innovationNumber;
	public CalcConnectionList connections = new CalcConnectionList();
	public float value;
	public boolean input;
	public float x;
	public boolean calculated = true;
	
	public CalcNode(long innovationNumber, boolean enabled, ActivationFunction activationFunction, AggregationFunction aggregationFunction, boolean input, float x) {
		this.innovationNumber = innovationNumber;
		this.enabled = enabled;
		this.activationFunction = activationFunction;
		this.aggregationFunction = aggregationFunction;
		this.input = input;
		this.x = x;
	}
	
	public float calculate() {
		if ((!input) & (!calculated)) {
			List<Float> values = new ArrayList();
			for (CalcConnection connection : connections.getSet()) {
				if (!connection.enabled) {
					float value = aggregationFunction.connectionValDisabled();
					if (!Float.isNaN(value)) {
						values.add(value);
					}
				}
				else {
					float value = connection.calculate();
					if (!Float.isNaN(value)) {
						values.add(value);
					}
				}
			}
			float val = aggregationFunction.calculate(values.toArray());
			value = activationFunction.calculate(val);
			calculated = true;
			return value;
		}
		else {
			return value;
		}
	}
	
}
