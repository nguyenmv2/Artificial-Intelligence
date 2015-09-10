package planner.core;

import java.util.*;

public class PlanGraph {
	private final static boolean DEBUG = false;
	private static HashMap<State,State> state2newState = new HashMap<>();
	private static HashMap<State,ArrayList<Action>> state2newLevel = new HashMap<>();
	
	private Set<Action> used;
	
	private ArrayList<ArrayList<Action>> actions;
	private Map<Predicate,Action> firstAdders;
	private State start, goals;
	private boolean allGoalsReached;
	
	public boolean allGoalsReached() {return allGoalsReached;}
	
	public Plan extractNoDeletePlan() {
		return extractNoDeletePlan(start);
	}
	
	public static Plan makeNoDeletePlan(Domain d, State current, Problem p) {
		return new PlanGraph(d, current, p).extractNoDeletePlan();
	}
	
	public Plan extractNoDeletePlan(State current) {
		// Stub implementation.
		Stack<Action> actionList = new Stack<>();
		Queue<Predicate> predList = new LinkedList<>();
        goals.iterator().forEachRemaining(p -> predList.add(p));
        while (!predList.isEmpty())
        {
            Predicate p = predList.remove();
            if (!current.predIsTrue(p)){
                Action subsequenceAction = firstAdders.get(p);
                actionList.add(subsequenceAction);
                subsequenceAction.getPreconditions().iterator().forEachRemaining(pred -> predList.add(pred));
            }
        }
        NoDeletePlan noDelPlan = new NoDeletePlan();

        while(!actionList.isEmpty())
        {
            noDelPlan.appendAction(actionList.pop());
        }
        return noDelPlan;

		// TODO: Implement this method.
		// Then, use a no-delete-plan as a heuristic estimate.
		
		// To implement this method:
		// - Create a collection of predicates.  Initialize it with the goals.
		// - Create an empty list of actions.  (This will ultimately be the plan.)
		// - Loop as long as there are predicates in that collection.
		//   - On each loop iteration:
		//     - Pick a predicate and remove it from the collection.
		//     - If the predicate is not already true, use the firstAdders
		//       object to find the first Action that added it.
		//     - Add that Action to the start of your list of actions.
		//     - Add all of the Action's preconditions to the collection of predicates.
		// - Create an empty NoDeletePlan.
		// - Add every Action from the action list to this plan, unless the plan already 
		//   contains that Action.
	}
	
	public PlanGraph(Domain d, State current, Problem p) {
		used = new HashSet<Action>();
		
		start = current;
		goals = p.getGoals();
		actions = new ArrayList<ArrayList<Action>>();
		firstAdders = new HashMap<Predicate,Action>();
		while (!current.allGoalsMet(goals)) {
			State prev = current;
			current = addNewLevel(current, d);
			if (prev.equals(current)) {
				allGoalsReached = false;
				return;
			}
		}
		allGoalsReached = true;
		if (DEBUG) {System.out.println("levels: " + actions.size());}
	}

	private State addNewLevel(State current, Domain d) {
		if (state2newState.containsKey(current)) {
			ArrayList<Action> level = state2newLevel.get(current);
			actions.add(level);
			for (Action act: level) {
				used.add(act);
				updateFirstAdders(act);
			}
			return state2newState.get(current);
		} else {
			State startState = current;
			ArrayList<Action> level = new ArrayList<Action>();
			long start = System.currentTimeMillis();
			Set<Action> doable = d.makeInstantiatedActions(current);
			if (DEBUG) System.out.println("To make actions took " + (System.currentTimeMillis() - start) + " ms");
			for (Action act: doable) {
				if (!used.contains(act)) {
					used.add(act);
					level.add(act);
					current = applyAction(act, current);
				}
			}
			actions.add(level);
			if (DEBUG) {System.out.println("level: " + actions.size() + " width: " + level.size() + " states: " + current.size() + " actions: " + doable.size());}
			state2newState.put(startState, current);
			state2newLevel.put(startState, level);
			return current;
		}
	}
	
	private void updateFirstAdders(Action act) {
		for (Predicate added: act.getAddEffects()) {
			if (!firstAdders.containsKey(added)) {
				firstAdders.put(added, act);
			}
		}
	}
	
	private State applyAction(Action act, State current) {
		updateFirstAdders(act);
		return new State(current, act.getAddEffects());
	}
	
	public int getGraphDepth() {
		return actions.size();
	}
}
