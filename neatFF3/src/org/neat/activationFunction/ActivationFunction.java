package org.neat.activationFunction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.neat.neat.Neat;

/**
 * @author Quentin
 *
 */
public abstract class ActivationFunction {
	
	public Neat neat;
	
	/*public static ActivationFunction Step = new ActivationStep();
	public static ActivationFunction Sigmoid = new ActivationSigmoid();
	public static ActivationFunction ReLU = new ActivationReLU();
	public static ActivationFunction LeakyReLU = new ActivationLeakyReLU();
	public static ActivationFunction ArcTan = new ActivationArcTan();
	public static ActivationFunction SiLU = new ActivationSiLU();
	public static ActivationFunction ELU = new ActivationELU();
	public static ActivationFunction Softplus = new ActivationSoftplus();
	public static ActivationFunction Hat = new ActivationHat();
	public static ActivationFunction Abs = new ActivationAbs();
	public static ActivationFunction Clamped = new ActivationClamped();
	public static ActivationFunction Cube = new ActivationCube();
	public static ActivationFunction Square = new ActivationSquare();
	public static ActivationFunction Exp = new ActivationExp();
	public static ActivationFunction Gauss = new ActivationGauss();
	public static ActivationFunction Identity = new ActivationIdentity();
	public static ActivationFunction Inverted = new ActivationInverted();
	public static ActivationFunction Log = new ActivationLog();
	public static ActivationFunction Sin = new ActivationSin();
	public static ActivationFunction Tanh = new ActivationTanh();*/
	
	Map<String, ActivationParameter> parameters = new HashMap<>();
	
	public ActivationFunction(Neat neat) {
		this.neat = neat;
	}
	
	/**
	 * @param input Input Value
	 * @return Calculated Value
	 */
	public abstract float calculate(float input);
	
	public ActivationParameter getParameter(String name) {
		return parameters.get(name);
	}
	
	public void mutate() {
		List<ActivationParameter> mutatable = new ArrayList<>();
		for (ActivationParameter activationParameter : parameters.values()) {
			if (activationParameter.canMutate) {
				mutatable.add(activationParameter);
			}
		}
		int count = (int) Math.floor(neat.Gaussian()*neat.config.mutate.averageMutateNodesActivationParametersPerMutation);
		count = Math.min(count, mutatable.size());
		Collections.shuffle(mutatable);
		for (int k = 0; k < count; k++) {
			ActivationParameter parameter = mutatable.remove(0);
			parameter.mutate();
		}
	}
	
	public ActivationFunction copy() {
		ActivationFunction copy = createNew();
		copy.parameters.clear();
		for (Entry<String, ActivationParameter> entry : parameters.entrySet()) {
			copy.parameters.put(entry.getKey(), entry.getValue().copy());
		}
		return copy;
	}
	
	public abstract ActivationFunction createNew();
	
}
