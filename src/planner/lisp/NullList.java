package planner.lisp;


public class NullList extends LispList {

    public boolean isAtom () {return true;}
    public boolean isNull () {return true;}

    // Pre: none
    // Post: Returns the string representation of this LispList
    public String toString() {return "()";}

    // Pre: none
    // Post: Returns the number of non-null atoms in this LispList
    public int numAtoms() {return 0;}

    // Pre: none
    // Post: Returns the number of atoms matching s in this LispList
    public int numAtoms (String s) {return 0;}

    // Pre: none
    // Post: Converts all strings in this LispList to lower case
    public void toLower() {}
}
