package planner.heuristics;

import planner.core.NoDeletePlan;
import planner.core.Plan;
import planner.core.PlanGraph;
import planner.core.PlanStep;
import search.core.BestFirstHeuristic;

/**
 * Created by nguye on 9/10/2015.
 */
public class RelaxedPlan implements BestFirstHeuristic<PlanStep> {
    @Override
    public int getDistance(PlanStep node, PlanStep goal) {
        PlanGraph pg = new PlanGraph(node.getDomain(),node.getProblem().getStartState(),node.getProblem());
        Plan d = pg.extractNoDeletePlan(node.getProblem().getStartState());
        return d.length();
    }
}
