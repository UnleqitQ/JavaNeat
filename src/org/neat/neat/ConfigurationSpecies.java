package org.neat.neat;

public class ConfigurationSpecies {
	
	public boolean specicate = true;
	
	public float compatibilityDisjointConnectionCoefficient = 1.0f;
	public float compatibilityDisjointNodeCoefficient = 1.0f;
	//public float compatibilityWeightCoefficient = 0.5f;
	public float compatibilityConnectionWeightCoefficient = 0.5f;
	public float compatibilityNodeWeightCoefficient = 0.5f;
	
	public float compatibilityThreshold = 3.0f;
	
	//public int maxStagnation = 20;
	public int speciesElitism = 2;
	
	public float speciesProgressInterval = 0.1f;
	public int stagnationDuration = 15;
	
}
