package org.neat.aggregationFunction;

/**
 * @author Quentin
 *
 */
public interface AggregationFunction {
	
	public static AggregationFunction max = new AggregationMax();
	public static AggregationFunction min = new AggregationMin();
	public static AggregationFunction maxAbs = new AggregationMaxAbs();
	public static AggregationFunction mean = new AggregationMean();
	public static AggregationFunction median = new AggregationMedian();
	public static AggregationFunction sum = new AggregationSum();
	public static AggregationFunction sumSqr = new AggregationSumSqr();
	public static AggregationFunction sumCube = new AggregationSumCube();
	public static AggregationFunction mul = new AggregationMul();
	public static AggregationFunction standardDeviation = new AggregationStandardDeviation();
	public static AggregationFunction range = new AggregationRange();
	
	/**
	 * Used to combine multiple Values
	 * @param values Values to combine
	 * @return Calculated Value
	 */
	public float calculate(Object... values);

	/**
	 * Value to add to Aggregation Function, if Connection is disabled (Default: 0)<br>
	 * if {@linkplain java.lang.Float#NaN}, Connection is ignored
	 * @return Value if Connection is disabled
	 */
	public default float connectionValDisabled() {
		return 0;
	}
	/**
	 * Value to use as input for Connection (multiplied with {@linkplain org.neat.gene.ConnectionGene#weight weight}), if Node is disabled (Default: 0)<br>
	 * if {@linkplain java.lang.Float#NaN}, Connection returns {@linkplain java.lang.Float#NaN} and is ignored
	 * @return Value if Node is disabled
	 */
	public default float nodeValDisabled() {
		return 0;
	}
	
	
	
}
