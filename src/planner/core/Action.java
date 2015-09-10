package planner.core;
import java.util.*;

import planner.lisp.Atom;
import planner.lisp.LispList;
import planner.lisp.NullList;
import planner.lisp.Pair;

public class Action implements Comparable<Action> {
	private String name;
	private List<String> params;
	private State preconds;
	private Conjunction effects;
	
	private final static boolean debug = false;

	// Invariants
	// Any Action objects with the same name will have the same
	// generic preconditions, add lists, and delete lists and the same
	// number of parameters

	// Pre: act is in the following format
	// (:action name :parameters ([paramList]) 
	//               :precondition (and [conditions])
	//               :effect (and [effects])
	// Post: Corresponding Action object built
	public Action (LispList act) {
		if (debug) {System.out.println("Building action " + act);}
		checkHead (act, "action");
		act = act.rest();
		name = act.first().toString();
		act = act.rest();
		checkHead (act, "parameters");
		act = parseParams (act);
		checkHead (act, "precondition");
		act = parsePrecondition (act);
		checkHead (act, "effect");
		act = parseEffect (act);
	}  

	// Pre: bindings != null
	// Post: Constructs a new Action based on orig with all keys from 
	//       bindings replaced by the corresponding values
	public Action (Action orig, Map<String,String> bindings) {
		name = orig.name;
		params = new ArrayList<String>(orig.params.size());
		for (String param: orig.params) {
			String bound = bindings.get(param);
			params.add((bound == null) ? param : bound);
		}
		params = Collections.unmodifiableList(params);

		preconds = new State (orig.preconds, bindings);
		effects = new Conjunction (orig.effects, bindings);
	}

	public Map<String,String> makeBindingsFrom(String... objects) {
		if (objects.length != params.size()) {
			throw new IllegalArgumentException("Wrong number of objects");
		}
		Map<String,String> bindings = new HashMap<String,String>();
		for (int i = 0; i < objects.length; ++i) {
			bindings.put(params.get(i), objects[i]);
		}
		return bindings;
	}

	public Action (Action orig, String... objects) {
		this(orig, orig.makeBindingsFrom(objects));
	}
 	
 	public boolean isNamed(String name) {
 		return this.name.equals(name);
 	}

	// Pre: None
	// Post: Returns true if this Action has the specified precondition
	public boolean hasPrecond (Predicate pre) {
		return preconds.predIsTrue (pre);
	}

	// Pre: None
	// Post: Returns true if this deletes pred
	//       Note that if pred is negative, adding pred "deletes" it
	public boolean deletes (Predicate pred) {
		if (pred != null) {return adds (pred.makeNegated());}
		return false;
	}
	
	// Pre: None
	// Post: Returns true if this adds pred
	//       Note that if pred is negative, deleting pred "adds" it
	public boolean adds (Predicate pred) {
		if (pred != null) {
			for (Predicate p: getEffects()) {
				if (pred.equals(p)) {return true;}
			}
		}
		return false;
	}

	// Pre: None
	// Post: Returns true if adds(p) is true for all p in preds
	public boolean addsAll (Iterable<Predicate> preds) {
		for (Predicate p: preds) {
			if (!adds(p)) {return false;}
		}
		return true;
	}

	// Pre: None
	// Post: Returns true if adds(p) is true for any p in preds
	public boolean addsAny (Iterable<Predicate> preds) {
		for (Predicate p: preds) {
			if (adds(p)) {return true;}
		}
		return false;
	}

	// Pre: Every effect of this and other is unique
	//      (There is no reason for this not to be true)
	// Post: Returns true if every effect of this is negated in other,
	//       and vice versa
	public boolean isInverseOf (Action other) {
		if (effects.numPredicates() != other.effects.numPredicates()) {
			return false;
		}

		for (Predicate myEffect: getEffects()) {
			boolean inverseFound = false;
			for (Predicate otherEffect: other.getEffects()) {
				inverseFound = inverseFound || myEffect.negates (otherEffect);
			}
			if (!inverseFound) {return false;}
		}
		return true;
	}

	public State getPreconditions() {return new State(preconds);}
	public List<Predicate> getEffects() {return effects.getPreds();}
	public List<Predicate> getAddEffects() {return effects.getPositivePreds();}
	public List<Predicate> getDeleteEffects() {
		return effects.getNegativePreds();
	}

	// Pre: None
	// Post: Returns true if this and other have the same names, the
	//       same number of arguments, and all preconditions and
	//       effects match (as defined for a Predicate)
	public boolean matches (Action other) {
		return ((other != null) && (name.equals (other.name)) && 
				(params.size() == other.params.size()) && 
				preconds.equals (other.preconds) &&
				effects.matches (other.effects));
	}

	// Pre: matches (other)
	// Post: Returns a Map from each argument of this to each argument
	//       of other
	public Map<String,String> bindingsFor (Action other) {
		Map<String,String> result = new HashMap<String,String>();
		for (int i = 0; i < params.size(); ++i) {
			result.put (params.get(i), other.params.get(i));
		}
		return result;
	}

	// Pre: bindings != null
	// Post: Returns true if any parameters are absent from bindings;
	//       Returns false otherwise
	public boolean anyUnbound (Map<String,String> bindings) {
		for (String param: params) {
			if (bindings.get(param) == null) {return true;}
		}
		return false;
	}

	// Pre: bindings != null
	// Post: Returns a list of all parameters that are absent from bindings
	public String[] stillUnbound (Map<String,String> bindings) {
		int count = 0;
		for (int i = 0; i < params.size(); ++i) {
			if (bindings.get(params.get(i)) == null) {++count;}
		}

		String result[] = new String[count];
		for (int i = 0, j = 0; i < params.size() && j < count; ++i) {
			if (bindings.get(params.get(i)) == null) {
				result[j] = params.get(i);
				++j;
			}
		}
		return result;
	}

	// Pre: none
	// Post: Returns a new Action with all the parameters rebound
	//       (This is only for testing purposes)
	public Action testBindings() {
		TreeMap<String,String> t = new TreeMap<String,String>();
		for (int i = 0; i < params.size(); ++i) {
			t.put (params.get(i), Integer.toString(i));
		}

		return new Action(this, t);
	}

	// Pre: state != null
	// Post: Returns true if all preconds are true in state
	//       Returns false otherwise
	public boolean isLegal(State state){ return state.allGoalsMet(preconds); }

	// Pre: this.isLegal (prev)
	// Post: Returns a new State resulting from applying Action
	public State apply (State prev) {
		return new State(prev, getEffects());
	}

	// Pre: None
	// Post: Two Actions are identical if name.compareTo (other.name) == 0
	//       and if the same holds for every element of params.
	//       Otherwise, the first nonzero compareTo is returned.
	public int compareTo (Action other) {
		int comp = name.compareTo (other.name);
		if (comp == 0) {
			comp = params.size() - other.params.size();
			for (int i = 0; i < params.size() && comp == 0; ++i) {
				comp = params.get(i).compareTo (other.params.get(i));
			}
		}
		return comp;
	}

	// Pre: None
	// Post: Returns true if compareTo(other) == 0; returns false otherwise
	public boolean equals (Object other) {
		if (other == null) return false;
		try {
			Action oth = (Action)other;
			return (compareTo(oth) == 0);
		} catch (ClassCastException e) {
			return false;
		}
	}

	// Pre: None
	// Post: Returns a hash code consistent with equals()
	public int hashCode() {return nameAndParams().hashCode();}

	// Pre: none
	// Post: Returns a String corresponding to this Action
	public String toString() {return toLispList().toString();}

	// Pre: none
	// Post: Returns a String displaying only the name and parameters
	public String nameAndParams() {
		String result = '(' + name;
		for (int i = 0; i < params.size(); ++i) {
			result += ' ' + params.get(i); 
		}
		result += ')';
		return result;
	}

	public List<String> getParameters(){ return params; }

	// Pre: none
	// Post: Returns a LispList corresponding to this Action
	public LispList toLispList () {
		LispList result = new Pair (effects.toLispList(), new NullList());
		result = new Pair (new Atom (":effect"), result);
		LispList precondList = preconds.toLispList();
		if (preconds.size() > 1) {
			precondList = new Pair(new Atom("and"), precondList);
		} else {
			precondList = precondList.first();
		}
		result = new Pair (precondList, result);
		result = new Pair (new Atom (":precondition"), result);
		result = new Pair (makeParamList(), result);
		result = new Pair (new Atom (":parameters"), result);
		result = new Pair (new Atom (name), result);
		result = new Pair (new Atom (":action"), result);
		return result;
	}
	
	private ArrayList<Map<String,String>> allBindingsIn(State current, Map<String,String> start) {
		ArrayList<Map<String,String>> allBindings = new ArrayList<Map<String,String>>();
		allBindings.add(start);
		for (Predicate precond: preconds) {
			ArrayList<Map<String,String>> updated = new ArrayList<Map<String,String>>();
			for (Map<String,String> binding: allBindings) {
				if (precond.allParamsBound(binding)) {
					updated.add(binding);
				} else {
					updated.addAll(current.allBindingsFor(precond, binding));
				}
			}
			allBindings = updated;
		}
		assertFullyBound(allBindings);
		return allBindings;
	}
	
	private void assertFullyBound(ArrayList<Map<String,String>> allBindings) {
		for (Map<String,String> binding: allBindings) {
			if (anyUnbound(binding)) {
				System.out.println(binding);
				System.out.println(this);
				throw new IllegalStateException("Missed some!");
			}
		}
	}
	
	public Set<Action> allInstantiationsOf(State current) {
		return allInstantiationsOf(current, new HashMap<String,String>());
	}
	
	public Set<Action> allInstantiationsOf(State current, Map<String,String> binding) {
		Set<Action> result = new LinkedHashSet<Action>();
		for (Map<String,String> bindings: allBindingsIn(current, binding)) {
			Action candidate = new Action(this, bindings);
			if (candidate.isLegal(current)) {
				result.add(candidate);
			}
		}
		return result;
	}
	
	public Map<String,String> bindingsFrom(Predicate bound) {
		for (Predicate pre: preconds) {
			if (pre.matches(bound)) {
				return pre.bindingsFor(bound);
			}
		}
		throw new IllegalArgumentException(bound + " is not a precondition of " + nameAndParams());
	}

	private LispList makeParamList () {
		LispList result = new NullList();
		for (int i = params.size() - 1; i >= 0; --i) {
			result = new Pair (new Atom (params.get(i)), result);
		}
		return result;
	}

	// Pre: act.first().equals (":parameters")
	// Post: act.first().equals (":precondition");
	//       all parameters are stored in params[]  
	private LispList parseParams (LispList act) {
		act = act.rest();
		LispList paramList = act.first();
		act = act.rest();

		params = new ArrayList<String>();
		while (!paramList.isNull()) {
			params.add(paramList.first().toString());
			paramList = paramList.rest();
		}

		return act;
	}

	// Pre: act.first().equals (":precondition")
	// Post: act.first().equals (":effects");
	//       all preconditions stored in preconds
	private LispList parsePrecondition (LispList act) {
		if (debug) {System.out.println("act.first(): " + act.first());}
		act = act.rest();
		LispList stateList = act.first();
		if (debug) {System.out.println("stateList: " + stateList);}
		if (stateList.first().toString().equals("and")) {
			preconds = new State(stateList.rest());
		} else {
			preconds = new State(new Predicate(stateList));
		}
		if (debug) {System.out.println("preconds: " + preconds);}
		act = act.rest();
		return act;
	}

	// Pre: act.first().equals (":effects");
	// Post: act.isNull(); all effects stored in effects
	private LispList parseEffect (LispList act) {
		act = act.rest();
		effects = new Conjunction (act.first());
		act = act.rest();
		return act;
	}

	private void checkHead (LispList act, String label) {
		if (!act.first().toString().equals (":" + label)) {
			parseError ("No " + label + " clause in " + act.toString());
		}
	}

	private void parseError (String msg) {
		System.out.println (msg);
		System.exit (1);
	}
}
