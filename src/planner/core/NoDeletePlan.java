package planner.core;

public class NoDeletePlan extends Plan {
	
	public static Plan buildFrom(String planStr, Domain d) {
		return Plan.buildFrom(planStr, d, new NoDeletePlan());
	}
	
	@Override
	public State applyAction(int step, State current) {
		return new State(current, get(step).getAddEffects());
	}
}
