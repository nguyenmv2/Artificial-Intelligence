package planner.core;

import java.util.*;

public class Plan implements Iterable<Action> {

	private ArrayList<Action> planSteps = new ArrayList<Action>();
	
	protected static Plan buildFrom(String planStr, Domain d, Plan plan) {
		for (String line: planStr.split("\\n")) {
			line = line.substring(1, line.length() - 1).toLowerCase();
			String name = line.substring(0, line.indexOf(' '));
			String[] objs = line.substring(line.indexOf(' ') + 1).split(" ");
			plan.appendAction(d.instantiateAction(name, objs));
		}
		return plan;
	}
	
	public static Plan buildFrom(String planStr, Domain d) {
		return buildFrom(planStr, d, new Plan());
	}

	// Pre: None
	// Post: Returns number of operators in this Plan
	public int length() {return planSteps.size();}
	
	public Action get(int i) {return planSteps.get(i);}

	// Pre: act != null
	// Post: Adds act as final operator on Plan
	public void appendAction(Action act) {
		if (act != null) {planSteps.add(act);}
	}

	// Pre: None
	// Post: Returns a String containing one instantiated operator
	//       per line
	public String toString() {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < planSteps.size(); ++i) {
			Action step = planSteps.get(i);
			result.append (step.nameAndParams());
			result.append ("\n");
		}
		return result.toString();
	}
	
	public State applyAction(int step, State current) {
		return get(step).apply(current);
	}

	// Pre: None
	// Post: Returns a String containing a description of this Plan's
	//       legality and validity
	public String makeReport(Problem prob) {
		State current = prob.getStartState();
		for (int i = 0; i < length(); ++i) {
			if (!get(i).isLegal(current)) {
				return "Action " + get(i).nameAndParams() + " is illegal; unmet preconditions: " + current.unmetGoals(get(i).getPreconditions());
			}
			current = applyAction(i, current);
		}
		return "Plan is legal, " + (prob.goalsMet(current) ? "and goals are" : "but goals are not") + " met.";
	}

	// Pre: startingState != null
	// Post: Returns true if all operators in this plan are legally applied
	public boolean isPlanLegal(State startingState){
		return getFinalState (startingState) != null;
	}

	// Pre: p != null
	// Post: Returns true if all operators are legally applied and all
	//       goals have been achieved
	public boolean isPlanValid(Problem p) {
		State finalState = getFinalState (p.getStartState());
		return finalState != null && p.goalsMet(finalState);
	}
	
	public Set<Predicate> unachievedGoals(Problem p) {
		State end = getFinalState(p.getStartState());
		return end.unmetGoals(p.getGoals());
	}
	
	public boolean contains(Action act) {
		return planSteps.contains(act);
	}

	// Pre: startingState != null
	// Post: Returns the final state resulting from applying this plan
	//       to startingState if all operator applications are legal;
	//       otherwise returns null
	public State getFinalState(State startingState) {
		State currentState = startingState;

		for(int i = 0; i < planSteps.size(); ++i){
			Action nextStep = planSteps.get(i);
			if(!nextStep.isLegal(currentState)) {return null;}
			currentState = applyAction(i, currentState);
		}

		return currentState;	
	}

	@Override
	public Iterator<Action> iterator() {
		return planSteps.iterator();
	}
}
