package org.neat.activationFunction;

/**
 * @author Quentin
 *
 */
public interface ActivationFunction {
	
	public static ActivationFunction Step = new ActivationStep();
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
	public static ActivationFunction Tanh = new ActivationTanh();
	
	
	/**
	 * @param input Input Value
	 * @return Calculated Value
	 */
	public float calculate(float input);
	
}
