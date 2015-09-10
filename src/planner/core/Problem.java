package planner.core;

import java.io.File;
import java.util.*;

import planner.lisp.Atom;
import planner.lisp.LispList;
import planner.lisp.Pair;
import planner.lisp.NullList;

//Represents a specific planning problem instance
public class Problem {
	private String problemName;
	private String domainName;
	private List<String> objects;
	private State startState;
	private State goals;
	
	private static final boolean DEBUG = false;
	
	public Problem (String filename) {
		this(new File(filename));
	}
	
	public Problem (File f) {
		this(LispList.fromFile(f));
	}

	public Problem (String probName, String domName, String[] objs, 
			State start, State goalCond) {
		problemName = probName;
		domainName = domName;
		objects = Collections.unmodifiableList(Arrays.asList(objs));
		startState = start;
		goals = goalCond;
	}

	public Problem (LispList probList) {
		probList.toLower();
		checkHead (probList, "define");
		probList = probList.rest();

		LispList pList = probList.first();
		checkHead (pList, "problem");
		problemName = pList.rest().first().toString();
		probList = probList.rest();

		LispList domainList = probList.first();
		checkHead (domainList, ":domain");
		domainName = domainList.rest().first().toString();
		probList = probList.rest();

		LispList objList = probList.first();
		checkHead (objList, ":objects");
		objList = objList.rest();
		objects = new ArrayList<String>();
		int target = objList.length();
		for (int i = 0; i < target; ++i, objList = objList.rest()) {
			objects.add(objList.first().toString());
		}
		probList = probList.rest();

		LispList stateList = probList.first();
		checkHead (stateList, ":init");
		startState = new State (stateList.rest());
		probList = probList.rest();

		LispList goalList = probList.first().rest().first();
		if (DEBUG) {System.out.println("goalList: " + goalList);}
		if (goalList.first().toString().equals("and")) {
			goals = new State(goalList.rest());
		} else {
			goals = new State(new Predicate(goalList));
		}
	}

	public List<String> getObjects() {return objects;}
	public State getStartState() {return startState;}
	public String getProblemName() {return problemName;}
	public String getDomainName() {return domainName;}
	public State getGoals(){ return goals; }

	public boolean goalsMet (State current) {
		return current.allGoalsMet(goals);
	}

	public int numGoals() {return goals.size();}

	public String toString() {return toLispList().toString();}

	public LispList toLispList() {
		LispList result = new NullList();
		result = new Pair (makeGoalList(), result);
		result = new Pair (makeStateList(), result);
		result = new Pair (makeObjList(), result);
		result = new Pair (makeDomainList(), result);
		result = new Pair (makeNameList(), result);
		result = new Pair (new Atom ("define"), result);
		return result;
	}

	public static void main (String[] args) {
		LispList probList = LispList.fromFile (args[0]);
		Problem p = new Problem (probList);
		Problem q = new Problem (p.toLispList());
		if (p.toString().equals (q.toString())) {
			System.out.println ("Parsing works");
		} else {
			System.out.println ("Parsing has a bug");
		}
		System.out.println ("Problem:");
		System.out.println (p.toString());
	}

	private LispList makeGoalList() {
		if (goals.size() > 1) {
			return new Pair (new Atom (":goal"), 
					new Pair (new Pair (new Atom("and"), goals.toLispList()), new NullList()));
		} else {
			return new Pair (new Atom(":goal"), new Pair(goals.iterator().next().toLispList(), new NullList()));
		}
	}

	private LispList makeStateList() {
		return new Pair (new Atom (":init"), startState.toLispList());
	}

	private LispList makeObjList() {
		LispList result = new NullList();
		for (int i = objects.size() - 1; i >= 0; --i) {
			result = new Pair (new Atom (objects.get(i)), result);
		}
		result = new Pair (new Atom (":objects"), result);
		return result;
	}

	private LispList makeDomainList() {
		return new Pair (new Atom (":domain"), 
				new Pair (new Atom (domainName), new NullList()));
	}

	private LispList makeNameList() {
		return new Pair (new Atom ("problem"),
				new Pair (new Atom (problemName), new NullList()));
	}

	private void checkHead (LispList act, String label) {
		if (!act.first().toString().equals (label)) {
			parseError ("No " + label + " clause in " + act.toString());
		}
	}

	private void parseError (String msg) {
		System.out.println (msg);
		System.exit (1);
	}
}
