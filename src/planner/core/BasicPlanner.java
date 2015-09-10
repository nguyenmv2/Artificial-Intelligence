package planner.core;

import search.core.BestFirstHeuristic;
import search.core.BestFirstObject;
import search.core.BestFirstSearcher;

abstract public class BasicPlanner<T extends BestFirstObject<T>> implements Planner {
	private BestFirstSearcher<T> searcher;
	
	public BasicPlanner(BestFirstHeuristic<T> bfh) {
		searcher = new BestFirstSearcher<T>(bfh);
	}
	
	abstract protected T makeStart(Domain d, Problem p);
	abstract protected T makeTarget(Domain d, Problem p);
	abstract protected Action getGeneratingAction(BestFirstSearcher<T> bfs, int i);
	
	public Plan makePlan(Domain d, Problem p) {
		Plan result = new Plan();
		searcher.solve(makeStart(d, p), makeTarget(d, p));
		if (searcher.success()) {
			for (int i = 0; i < searcher.numSteps(); ++i) {
				result.appendAction(getGeneratingAction(searcher, i));
			}
		} 
		return result;
	}
	
	public int getNumNodes() {
		return searcher.getNumNodes();
	}
	
	public double getBranchingFactor() {
		return searcher.getBranchingFactor(0.001);
	}
	
	public int getMaxDepth() {
		return searcher.getMaxDepth();
	}
}
