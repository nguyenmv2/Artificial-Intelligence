package planner.core;

import java.util.ArrayList;

import search.core.BestFirstObject;


public class PlanStep extends BasicPlanStep implements BestFirstObject<PlanStep> {
	public PlanStep (State s, Domain d, Problem p) {
		this(s, d, p, null);
	}
	
	public PlanStep (State s, Domain d, Problem p, Action generator) {
		super(s, d, p, generator);
	}

	@Override
	public ArrayList<PlanStep> getSuccessors() {
		ArrayList<PlanStep> successors = new ArrayList<PlanStep>();
		for (Action act: getDomain().makeInstantiatedActions(getWorldState())) {
			successors.add(new PlanStep(act.apply(getWorldState()), getDomain(), getProblem(), act));
		}
		return successors;
	}

	public boolean achieves(PlanStep goal) {
		return getWorldState().allGoalsMet(goal.getWorldState());
	}
}
