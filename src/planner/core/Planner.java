package planner.core;

public interface Planner {
	public Plan makePlan(Domain d, Problem p);
	
	public int getNumNodes();
	
	public double getBranchingFactor();
	
	public int getMaxDepth();
}
