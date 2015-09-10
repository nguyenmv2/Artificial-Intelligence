package planner.heuristics;

import planner.core.PlanStep;
import planner.core.State;
import search.core.BestFirstHeuristic;

/**
 * Created by nguye on 9/10/2015.
 */
public class UnmetGoal  implements BestFirstHeuristic<PlanStep> {
    @Override
    public int getDistance(PlanStep node, PlanStep goal) {
       return (node.getWorldState().unmetGoals(goal.getWorldState()).size());
    }

}
