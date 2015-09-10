package planner.core;
import java.io.File;
import java.util.*;

import planner.lisp.Atom;
import planner.lisp.LispList;
import planner.lisp.NullList;
import planner.lisp.Pair;

// Represents a STRIPS-style planning domain

public class Domain {

	private String name;
	private Predicate preds[];
	private List<Action> actions;
	private LispList requirements;
	
	private final static boolean debug = false;
	
	public Domain(String filename) {
		this(new File(filename));
	}
	
	public Domain(File f) {
		this(LispList.fromFile(f));
	}

	public Domain (LispList dList) {
		dList.toLower();
		if (debug) {System.out.println("Parsing " + dList);}
		checkHead (dList, "define");
		dList = dList.rest();

		LispList domainList = dList.first();
		checkHead (domainList, "domain");
		name = domainList.rest().first().toString();
		dList = dList.rest();

		LispList requireList = dList.first();
		checkHead (requireList, ":requirements");
		requirements = requireList;
		// At some future time, make sure only ":strips" is required
		dList = dList.rest();

		LispList predList = dList.first();
		checkHead (predList, ":predicates");
		parsePreds (predList.rest());
		dList = dList.rest();

		parseActions (dList);
	}
	
	public List<Action> getActions() {
		return actions;
	}

	public Action instantiateAction(String actionName, String... objects) {
		for (Action act: actions) {
			if (act.isNamed(actionName)) {
				return new Action(act, objects);
			}
		}
		throw new IllegalArgumentException("No match for action name " + actionName);
	}

	public Set<Action> makeInstantiatedActions(State current) {
		Set<Action> result = new LinkedHashSet<Action>();
		for (Action unAct: getActions()) {
			result.addAll(unAct.allInstantiationsOf(current));
		}
		return result;
	}

	public String getName() {return name;}
	
	public String toString() {return toLispList().toString();}

	public LispList toLispList() {
		LispList result = new NullList();
		for (int i = actions.size() - 1; i >= 0; --i) {
			result = new Pair (actions.get(i).toLispList(), result);
		}

		LispList predList = new NullList();
		for (int i = preds.length - 1; i >= 0; --i) {
			predList = new Pair (preds[i].toLispList(), predList);
		}
		predList = new Pair (new Atom (":predicates"), predList);
		result = new Pair (predList, result);
		result = new Pair (requirements, result);

		LispList domainList = new Pair (new Atom (name), new NullList());
		domainList = new Pair (new Atom ("domain"), domainList);
		result = new Pair (domainList, result);

		result = new Pair (new Atom ("define"), result);
		return result;
	}

	private void parsePreds (LispList predList) {
		preds = new Predicate[predList.length()];
		int pred = 0;
		while (!predList.isNull()) {
			preds[pred] = new Predicate (predList.first());
			++pred;
			predList = predList.rest();
		}
	}

	private void parseActions (LispList dList) {
		if (debug) {System.out.println("Action list: " + dList);}
		actions = new ArrayList<Action>(dList.numAtoms (":action"));
		while (!dList.isNull()) {
			if (debug) {System.out.println("Processing action " + dList);}
			actions.add(new Action(dList.first()));
			dList = dList.rest();
		}
		actions = Collections.unmodifiableList(actions);
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

