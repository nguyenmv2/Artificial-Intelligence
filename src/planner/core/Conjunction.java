package planner.core;
import java.util.*;

import planner.lisp.Atom;
import planner.lisp.LispList;
import planner.lisp.NullList;
import planner.lisp.Pair;

public class Conjunction implements Iterable<Predicate> {
	private Predicate[] preds;
	private Predicate[] positive;
	private Predicate[] negative;

	// Pre: cond should be in either one of two formats:
	//      1. a single predicate
	//      2. (and (pred1 ...) (pred2 ...) ... (predN ...))
	// Post: Creates an appropriate Conjunction object
	public Conjunction (LispList cond) {
		if (cond.first().toString().equals ("and")) {
			cond = cond.rest();
			preds = new Predicate[cond.length()];
			int i = 0;
			while (!cond.isNull()) {
				preds[i++] = new Predicate (cond.first());
				cond = cond.rest();
			}
		} else {
			preds = new Predicate[1];
			preds[0] = new Predicate (cond);
		}
		makePosAndNeg();
	}

	public Conjunction (Conjunction orig, Map<String,String> bindings) {
		preds = new Predicate[orig.preds.length];
		for (int i = 0; i < orig.preds.length; ++i) {
			preds[i] = new Predicate (orig.preds[i], bindings);
		}	
		makePosAndNeg();
	}

	public Conjunction (Predicate singleton) {
		preds = new Predicate[1];
		preds[0] = singleton;
		makePosAndNeg();
	}

	public Conjunction (Collection<Predicate> cp) {
		preds = new Predicate[cp.size()];
		int numPreds = 0;
		for (Iterator<Predicate> i = cp.iterator(); i.hasNext();) {
			preds[numPreds] = i.next();
			numPreds += 1;
		}
		makePosAndNeg();
	}

	// Pre: preds has just been updated
	// Post: Replace positive and negative
	private void makePosAndNeg() {
		int numAdd = 0, numDel = 0;
		for (Predicate p: preds) {
			if (p.isTrue()) {++numAdd;} else {++numDel;}
		}

		positive = new Predicate[numAdd];
		negative = new Predicate[numDel];
		int a = 0, d = 0;
		for (Predicate p: preds) {
			if (p.isTrue()) {positive[a++] = p;}
			else {negative[d++] = p;}
		}
	}

	// Pre: None
	// Post: Returns true if this and other share the same preds
	public boolean equals (Object oth) {
		try {
			Conjunction other = (Conjunction)oth;
			if (preds.length != other.preds.length) {return false;}
			for (Predicate myP: preds) {
				boolean found = false;
				for (Predicate otherP: other.preds) {
					found = found || otherP.equals(myP);
				}
				if (!found) {return false;}
			}
			return true;
		} catch (ClassCastException e) {
			return false;
		}
	}

	public Iterator<Predicate> iterator() {return new Iter();}
	public List<Predicate> getPreds() {return Arrays.asList(preds);}
	public List<Predicate> getPositivePreds() {return Arrays.asList(positive);}
	public List<Predicate> getNegativePreds() {return Arrays.asList(negative);}
	public int numPredicates() {return preds.length;}

	// Returns true if for each Predicate p in this and the corresponding Predicate q in other, p.matches(q)
	// Returns false otherwise
	public boolean matches (Conjunction other) {
		if (other != null && numPredicates() == other.numPredicates()) {
			for (int i = 0; i < numPredicates(); ++i) {
				if (!preds[i].matches (other.preds[i])) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public String toString() {return toLispList().toString();}

	public LispList toLispList () {
		LispList result = new NullList();
		for (int i = preds.length - 1; i >= 0; --i) {
			result = new Pair (preds[i].toLispList(), result);
		}
		result = new Pair (new Atom ("and"), result);
		return result;
	}

	///////////////////
	// Inner Classes //
	///////////////////
	private class Iter implements Iterator<Predicate> {
		private int index;

		public Iter() {index = 0;}
		public Predicate next() {return preds[index++];}
		public boolean hasNext() {return index < preds.length;}
		public void remove() {throw new UnsupportedOperationException();}
	}
}
