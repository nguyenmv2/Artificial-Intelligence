package planner.lisp;


public class Atom extends LispList {

    private String symbol;

    public Atom (String s) {symbol = s;}

    public boolean isAtom () {return true;}

    // Pre: none
    // Post: Returns the string representation of this LispList
    public String toString() {return symbol;}

    // Pre: none
    // Post: Returns the number of non-null atoms in this LispList
    public int numAtoms() {return 1;}

    // Pre: none
    // Post: Returns the number of atoms matching s in this LispList
    public int numAtoms(String s) {return s.equals(symbol) ? 1 : 0;}

    // Pre: none
    // Post: Converts all strings in this LispList to lower case
    public void toLower() {symbol = symbol.toLowerCase();}
}
