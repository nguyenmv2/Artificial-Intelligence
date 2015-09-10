package planner.lisp;
import java.util.StringTokenizer;
import java.io.*;


abstract public class LispList {
	// Pre: none
	// Post: Returns true if an atom, returns false otherwise
	public boolean isAtom () {return false;}

	// Pre: none
	// Post: Returns true if null, returns false otherwise
	public boolean isNull () {return false;}

	// Pre: none
	// Post: Returns true if a pair, returns false otherwise
	public boolean isPair () {return false;}

	// Pre: none
	// Post: Returns the string representation of this LispList
	abstract public String toString();

	// Pre: none
	// Post: Returns first element of this LispList 
	//       Returns null if not a Pair
	public LispList first() {return new NullList();}

	// Pre: none
	// Post: Returns the remaining elements of this LispList
	//       Returns null if not a Pair
	public LispList rest() {return new NullList();}

	// Pre: none
	// Post: Returns the number of top-level elements in this LispList
	//       If not a list, length is 0
	public int length() {return 0;}

	// Pre: none
	// Post: Returns the number of non-null atoms in this LispList
	abstract public int numAtoms();

	// Pre: none
	// Post: Returns the number of atoms matching s in this LispList
	abstract public int numAtoms (String s);

	// Pre: none
	// Post: Converts all strings in this LispList to lower case
	abstract public void toLower();

	// Pre: list is formatted with balanced parentheses and so forth
	// Post: Constructs a LispList corresponding to list
	public static LispList makeList (String list) {
		StringTokenizer listTokens = 
				new StringTokenizer (list, "( )\t\n\r", true);
		String[] listParts = new String[listTokens.countTokens()];
		int numTokens = 0;
		while (listTokens.hasMoreTokens()) {
			String next = listTokens.nextToken();
			if (!(next.equals(" ") || next.equals("\t") || 
					next.equals("\n") || next.equals("\r"))) {
				listParts[numTokens] = next;
				++numTokens;
			}
		}

		return parsedList (listParts, 0, numTokens - 1);
	}

	// Pre: list is in a text file, properly formatted
	// Post: Constructs a LispList corresponding to file contents,
	//       ignoring comments starting with a ';'
	public static LispList fromFile (String filename) {
		return fromFile(new File(filename));
	}

	public static LispList fromFile(File file){

		BufferedReader inFile;
		StringBuffer list = new StringBuffer();
		try {
			inFile = new BufferedReader (new FileReader (file));
			String input;
			while ((input = inFile.readLine()) != null) {
				list.append(killComments(input));
			} 
			inFile.close();
		} catch (IOException e) {
			System.err.println ("File error: " + e.toString());
			System.exit(1);
		}

		return LispList.makeList (list.toString());

	}

	// This is an input debugging routine
	private static void dumpTokens (String[] listParts, int left, int right) {
		for (int i = left; i <= right; ++i) {
			if (listParts[i].equals("(") || i == right ||
					(i < right && listParts[i+1].equals(")"))) {
				System.out.print (listParts[i]);
			} else {
				System.out.print (listParts[i] + " ");
			}
		}
		System.out.println();
	}

	// Pre: list is a string containing comments starting with a ';'
	//      and ending at the next endline
	// Post: returns a copy of list without any comments
	private static String killComments (String list) {
		StringBuffer result = new StringBuffer();
		boolean comment = false;
		for (int i = 0; i < list.length(); ++i) {
			char c = list.charAt(i);
			if (c == '\n') {System.out.println("!");}
			if (!comment && c == ';') {comment = true;}
			if (!comment) {result.append(c);}
			if (comment && c == '\n') {comment = false;}
		}
		return result.toString();
	}

	// Pre: Mismatched parentheses in listParts
	// Post: Prints error message and quits program
	private static void parenError (String[] listParts, int left, 
			int right) {
		System.out.print ("Mismatched parentheses for ");
		dumpTokens (listParts, left, right);
		System.exit(1);
	}

	// Pre: listParts[left].equals("("); listParts[right].equals(")");
	//      0 <= left < right < listParts.length
	// Post: Returns a LispList corresponding to the given structure
	private static LispList parsedList (String[] listParts, int left, 
			int right) {
		checkParseErrors (listParts, left, right);

		if (left == right - 1) {
			return new NullList();
		} else if (listParts[left+1].equals(".")) {
			return new Atom (listParts[left+2]);
		} else {
			LispList first = null;
			int newLeft;
			if (listParts[left+1].equals("(")) {
				int matchingRight = findMatchingRight (listParts, left+1); 
				if (matchingRight >= right) {
					parenError (listParts, left, right);
				}

				first = parsedList (listParts, left+1, matchingRight);
				newLeft = matchingRight;

			} else {
				first = new Atom (listParts[left+1]);
				newLeft = left + 1;
			}

			listParts[newLeft] = listParts[left];
			return new Pair (first, parsedList (listParts, newLeft, right));
		}
	}

	// Pre: None
	// Post: Prints errors and halts if preconditions for parsedList() 
	//       are violated
	private static void checkParseErrors (String[] listParts, int left, 
			int right) {
		if (left >= right) {
			System.out.println ("Internal error: left: " + 
					left + " right: " + right);
			System.exit(1);

		} else if (!listParts[left].equals("(") ||
				!listParts[right].equals(")") ||
				findMatchingRight (listParts, left) != right) {
			parenError (listParts, left, right);
		}
	}

	// Pre: listParts[leftParen].equals ("(")
	// Post: Returns index of matching right paren if present; 
	//       Returns out-of-range index if not present
	static private int findMatchingRight (String[] listParts, int leftParen) {
		int numOpen = 0;
		int i = leftParen;
		do {
			if (listParts[i].equals("(")) {++numOpen;} 
			else if (listParts[i].equals(")")) {--numOpen;}
			++i;
		} while (i < listParts.length && listParts[i] != null && numOpen > 0);
		return numOpen == 0 ? i - 1 : i;
	}

	public static void main (String[] args) {
		LispList list = args[0].equals("-f") 
				? fromFile(args[1]) : makeList(args[0]);
				System.out.println ("Parsed list: " + list.toString());
				System.out.println ("List contains " + list.numAtoms() + " atoms");
				System.out.println ("List has length " + list.length());
	}
}

