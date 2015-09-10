package planner.core;
import java.util.*;

import planner.lisp.Atom;
import planner.lisp.LispList;
import planner.lisp.NullList;
import planner.lisp.Pair;

public class Predicate implements Comparable<Predicate> {
	private String name;
	private List<String> paramList;
	private boolean negated;
	private int hashValue;

	public Predicate (LispList listRep) {
		name = listRep.first().toString();
		negated = name.equals ("not");
		if (negated) {
			listRep = listRep.rest().first();
			name = listRep.first().toString();
		}

		listRep = listRep.rest();
		paramList = new ArrayList<String>();
		while (!listRep.isNull()) {
			paramList.add(listRep.first().toString());
			listRep = listRep.rest();
		}
		paramList = Collections.unmodifiableList(paramList);

		makeHashCode();
	}
	
	public Predicate(Predicate that) {
		this(that.toLispList());
	}

	public Predicate (Predicate orig, Map<String,String> bindings) {
		name = orig.name;
		negated = orig.negated;
		paramList = new ArrayList<String>();
		for (String param: orig.paramList) {
			String bound = bindings.get(param);
			paramList.add((bound == null) ? param : bound);
		}
		
		paramList = Collections.unmodifiableList(paramList);
		makeHashCode();
	}

	public Predicate makeNegated() {
		Predicate result = new Predicate(this);
		result.negated = !negated;
		return result;
	}

	// Pre: None
	// Post: Returns true if this and other have the same name, same
	//       parameters, but different truth values.  Returns false otherwise.
	public boolean negates (Predicate other) {
		if ((compareNamesAndArgs (other) != 0) ||
				(negated == other.negated)) {
			return false;
		} else {
			return true;
		}
	}

	public boolean isTrue() {return !negated;}

	public LispList toLispList () {
		LispList result = new NullList();
		for (int i = paramList.size() - 1; i >= 0; --i) {
			result = new Pair (new Atom (paramList.get(i)), result);
		}
		result = new Pair (new Atom (name), result);

		if (negated) {
			LispList newRest = new Pair (result, new NullList());
			result = new Pair (new Atom ("not"), newRest);
		}
		return result;
	}

	public String toString() {return toLispList().toString();}

	public List<String> getParams() {return paramList;}
	public int numParams() {return paramList.size();}
	public String getName(){ return name; }
	
	public boolean allParamsBound(Map<String,String> bindings) {
		for (String param: paramList) {
			if (!bindings.containsKey(param)) {
				return false;
			}
		}
		return true;
	}

	// Pre: None
	// Post: Returns < 0 if name is less than other's name;
	//       if the names are the same but the first param to differ is less; 
	//       or if the Predicates are identical but this.isTrue() and
	//       !other.isTrue() 
	//       returns 0 if equal; 
	//       returns > 0 otherwise
	public int compareTo (Predicate other) {
		int compare = compareNamesAndArgs (other);
		if (compare != 0) {return compare;}
		if (isTrue() && !other.isTrue()) {return -1;}
		if (!isTrue() && other.isTrue()) {return 1;}
		return 0;
	}

	// Pre: other != null
	// Post: Returns 0 if this and other have the same name and arg names;
	//       Returns < 0 if name is less than other's name or if the
	//       names are the same but the first param to differ is less;
	//       Returns > 0 otherwise
	public int compareNamesAndArgs (Predicate other) {
		int compare = name.compareTo (other.name);
		if (compare != 0) {return compare;}
		for (int i = 0; i < paramList.size(); ++i) {
			compare = paramList.get(i).compareTo (other.paramList.get(i));
			if (compare != 0) {return compare;}
		}
		return 0;
	}

	// Pre: None
	// Post: Returns true if other is a Predicate and compareTo(other) == 0
	public boolean equals (Object other) {
		if (other == null) return false;
		try {
			Predicate oth = (Predicate)other;
			return (compareTo(oth) == 0);
		} catch (ClassCastException e) {
			return false;
		}
	}

	// Pre: None
	// Post: Returns a hash code consistent with equals()
	public int hashCode() {return hashValue;}
	private void makeHashCode() {hashValue = toString().hashCode();}

	// Pre: None
	// Post: Returns true if this and other have the same name and the
	//       same number of arguments; returns false otherwise
	public boolean matches (Predicate other) {
		if (other == null) return false;
		return ((paramList.size() == other.paramList.size()) &&
				(name.compareTo(other.name) == 0));
	}

	// Pre: matches(other)
	// Post: Returns a Map from each argument of this to each argument
	//       of other
	public Map<String,String> bindingsFor (Predicate other) {
		Map<String,String> result = new HashMap<String,String>();
		for (int i = 0; i < paramList.size(); ++i) {
			result.put (paramList.get(i), other.paramList.get(i));
		}
		return result;
	}
}
