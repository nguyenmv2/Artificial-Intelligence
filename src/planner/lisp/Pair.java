package planner.lisp;


public class Pair extends LispList {

	private LispList car;
	private LispList cdr;

	public Pair (LispList f, LispList r) {
		car = f;
		cdr = r;
	}

	// Pre: none
	// Post: Returns first element of this LispList
	public LispList first() {return car;}

	// Pre: none
	// Post: Returns the remaining elements of this LispList
	public LispList rest() {return cdr;}

	// Pre: l != null
	// Post: Sets the first of this LispList to l
	public void setFirst (LispList l) {car = l;}

	// Pre: l != null
	// Post: Sets the rest of this LispList to l
	public void setRest (LispList l) {cdr = l;}

	public boolean isPair () {return true;}

	// Pre: none
	// Post: Returns the string representation of this LispList
	public String toString() {
		return '(' + restString();
	}

	// Pre: An open paren has been printed for this pair
	// Post: Returns string representation of this pair
	public String restString() {
		String result = "";
		if (cdr.isNull()) {
			result += car.toString() + ')';
		} else if (cdr.isAtom()) {
			result += car.toString() + " . " + cdr.toString() + ')';
		} else {
			Pair restList = (Pair)cdr;
			result += car.toString() + ' ' + restList.restString();
		}
		return result;
	}

	// Pre: none
	// Post: Returns the number of top-level elements in this LispList
	public int length() {
		return 1 + rest().length();
	}

	// Pre: none
	// Post: Returns the number of non-null atoms in this LispList
	public int numAtoms() {return car.numAtoms() + cdr.numAtoms();}

	// Pre: none
	// Post: Returns the number of atoms matching s in this LispList
	public int numAtoms (String s) {return car.numAtoms(s) + cdr.numAtoms(s);}

	// Pre: none
	// Post: Converts all strings in this LispList to lower case
	public void toLower() {
		car.toLower();
		cdr.toLower();
	}
}
