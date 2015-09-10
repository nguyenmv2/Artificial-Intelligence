package planner.core;

public class BasicPlanStep {
	private State worldState;
	private Domain d;
	private Problem p;
	private Action generator;

	public BasicPlanStep (State s, Domain d, Problem p) {
		this(s, d, p, null);
	}
	
	public BasicPlanStep (State s, Domain d, Problem p, Action generator) {
		worldState = s;
		this.d = d;
		this.p = p;
		this.generator = generator;
	}
	
	public Domain getDomain() {return d;}
	
	public Problem getProblem() {return p;}
	
	public Action getGeneratingAction() {return generator;}

	public State getWorldState() {return worldState;}
	
	@Override
	public String toString() {
		String result = "PlanNode;State:" + worldState.toString();
		if (generator != null) {result += ";Generator:" + generator.nameAndParams();}
		return result;
	}
	
	@Override
	public int hashCode() {
		return worldState.hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof BasicPlanStep) {
			BasicPlanStep that = (BasicPlanStep)other;
			return this.worldState.equals(that.worldState);
		} else {
			return false;
		}
	}
}
