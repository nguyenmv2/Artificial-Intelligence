package planner.core;

import search.core.BestFirstHeuristic;
import search.core.BestFirstSearcher;

public class BestFirstPlanner extends BasicPlanner<PlanStep> {
	
	public BestFirstPlanner(BestFirstHeuristic<PlanStep> bfh) {
		super(bfh);
	}

	@Override
	protected PlanStep makeStart(Domain d, Problem p) {
		return new PlanStep(p.getStartState(), d, p);
	}

	@Override
	protected PlanStep makeTarget(Domain d, Problem p) {
		return new PlanStep(p.getGoals(), d, p);
	}

	@Override
	protected Action getGeneratingAction(BestFirstSearcher<PlanStep> bfs, int i) {
		return bfs.get(i).getGeneratingAction();
	}
}
