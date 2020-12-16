package org.neat.neat;

public class ConfigurationMutate {
	public boolean addNode = true;
	public boolean addLink = true;
	public boolean toggleNode = true;
	public boolean toggleLink = true;
	public boolean newWeight = false;
	public boolean mutateWeight = true;
	public boolean combineInLinks = true;
	public boolean combineOutLinks = true;
	
	/*public float addNodeProb = 0.025f;
	public float addLinkProb = 0.1f;
	public float toggleNodeProb = 0.08f;
	public float toggleLinkProb = 0.08f;*/
	public float newWeightProb = 0.05f;
	public float mutateWeightProb = 0.5f;
	
	public float newWeightInterval = 0.5f;
	public float mutateWeightInterval = 0.25f;
	public float newLinkWeightInterval = 0.75f;
	
	public boolean nodeAggregation = false;
	public boolean nodeActivation = false;
	public boolean nodeActivationParameters = false;
	public boolean genomeAggregation = false;
	public boolean genomeActivation = false;
	public boolean genomeActivationParameters = false;
	
	/*public float nodeAggregationProb = 0.01f;
	public float nodeActivationProb = 0.01f;*/
	public float genomeAggregationProb = 0.005f;
	public float genomeActivationProb = 0.005f;
	public float genomeActivationParametersProb = 0.005f;
	
	public boolean hasWeightBorders = true;
	public float weightBorders = 1;
	
	public boolean addExistingNode = false;
	public float addExistingNodeProb = 0.05f;
	
	public boolean randEveryPossibleLink = false;
	public boolean randEveryPossibleNode = false;
	public boolean randEveryPossibleExistingNode = false;
	public boolean randEveryPossibleNodeAggregation = false;
	public boolean randEveryPossibleNodeActivation = false;
	public boolean randEveryPossibleNodeActivationParameters = false;
	public boolean randEveryPossibleLinkToggle = false;
	public boolean randEveryPossibleNodeToggle = false;
	
	public float averageAddNodesPerMutate = 0.1f;
	public float averageAddExistingNodesPerMutate = 0.05f;
	public float averageAddConnectionsPerMutate = 0.5f;
	public float averageNewNodesAggregatonPerMutate = 0.01f;
	public float averageNewNodesActivationPerMutate = 0.01f;
	public float averageMutateNodesActivationPerMutate = 0.1f;
	public float averageMutateNodesActivationParametersPerMutation = 1f;
	public float averageToggleConnectionsPerMutate = 0.2f;
	public float averageToggleNodesPerMutate = 0.2f;
	public float averageCombineInConnectionsPerMutate = 0.2f;
	public float averageCombineOutConnectionsPerMutate = 0.2f;
}
