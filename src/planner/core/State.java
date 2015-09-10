package planner.core;
import java.util.*;

import planner.lisp.LispList;
import planner.lisp.NullList;
import planner.lisp.Pair;

public class State implements Comparable<State>, Iterable<Predicate> {
	private TreeSet<Predicate> preds = new TreeSet<Predicate>();
	private int hashValue;
	private boolean changed = true;

	public State () {}
	
	public State(Predicate p) {
		this();
		updatePred(p);
	}

	public State (LispList stateList) {
		while (!stateList.isNull()) {	    
			updatePred(new Predicate (stateList.first()));
			stateList = stateList.rest();
		}
	}
	
	public State(State orig, Map<String,String> bindings) {
		this();
		for (Predicate p: orig.preds) {this.updatePred(new Predicate(p, bindings));}
	}
	
	public State(State orig) {
		this();
		for (Predicate p: orig.preds) {this.updatePred(p);}
	}
	
	public State(State old, List<Predicate> updates) {
		this(old);
		for (Predicate p: updates) {this.updatePred(p);}
	}

	// Returns true if p is an element of this State
	// Returns false otherwise
	public boolean predIsTrue (Predicate p) {
		if (p.isTrue()) {
			return preds.contains(p);
		} else {
			return !preds.contains(p.makeNegated());
		}
	}
	
	// Returns a Set containing all Predicates that are true in goal and false in this
	public Set<Predicate> unmetGoals (State goal) {
		Set<Predicate> result = new TreeSet<Predicate>();
		for (Predicate p: goal.preds) {
			if (!this.predIsTrue(p)) {
				result.add(p);
			}
		}
		return result;
	}
	
	public ArrayList<Map<String,String>> allBindingsFor(Predicate unbound, Map<String,String> prevBindings) {
		ArrayList<Map<String,String>> result = new ArrayList<Map<String,String>>();
		for (Predicate pred: preds) {
			if (unbound.matches(pred)) {
				Predicate partial = new Predicate(pred, prevBindings);
				Map<String,String> more = unbound.bindingsFor(partial);
				for (Map.Entry<String,String> ent: prevBindings.entrySet()) {
					more.put(ent.getKey(), ent.getValue());
				}
				result.add(more);
			}
		}
		return result;
	}
	
	public int numGoalsMet(State goal) {
		int met = 0;
		for (Predicate p: goal.preds) {
			if (this.predIsTrue(p)) {
				met += 1;
			}
		}
		return met;
	}
	
	public boolean allGoalsMet(State goal) {
		return numGoalsMet(goal) == goal.size();
	}

	// if p.isTrue() and p is false in this State, add p to this
	// if !p.isTrue() and !p is true in this State, remove p from this
	// Otherwise, do nothing
	//    
	private void updatePred (Predicate p) {
		if (p.isTrue() && !predIsTrue(p)) {
			preds.add(p);

		} else if (!p.isTrue()) {
			p = p.makeNegated();
			if (predIsTrue(p)) {preds.remove(p);}
		}

		changed = true;
	}

	public String toString() {return toLispList().toString();}
	public int hashCode() {
		if (changed) {
			hashValue = toString().hashCode();
			changed = false;
		}
		return hashValue;
	}
	
	public int size() {return preds.size();}

	// Returns 0 if objects have the same predicates
	// Returns < 0 if this has fewer predicates or if the first predicate to differ is less than other's
	// Returns > 0 otherwise
	public int compareTo (State other) {
		if (size() != other.size()) {
			return size() < other.size() ? -1 : 1;
		}
		Iterator<Predicate> i = preds.iterator();
		Iterator<Predicate> j = other.preds.iterator();
		int compare = 0;
		while (i.hasNext() && compare == 0) {
			Predicate p = i.next(), q = j.next();
			compare = p.compareTo(q);
		}
		return compare;
	}

	@Override
	public boolean equals (Object other) {
		if (other instanceof State) {
			return (compareTo ((State)other) == 0);
		} else {
			return false;
		}
	}

	public LispList toLispList() {
		LispList result = new NullList();
		for (Predicate p: this.preds) {
			result = new Pair (p.toLispList(), result);
		}
		return result;
	}

	@Override
	public Iterator<Predicate> iterator() {
		return preds.iterator();
	}
}
