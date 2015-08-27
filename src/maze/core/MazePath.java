package maze.core;
import java.util.*;

import search.core.BestFirstSearcher;

public class MazePath {
    private ArrayList<MazeCell> path;
    private Set<MazeCell> included;
    
    public MazePath(int xStart, int yStart) {
        path = new ArrayList<MazeCell>();
        included = new HashSet<MazeCell>();
        append(new MazeCell(xStart, yStart));
    }
    
    public MazePath(BestFirstSearcher<MazeExplorer> searchResult, Maze m) {
    	this(m.getStart().X(), m.getStart().Y());
    	for (int i = 0; i < searchResult.numSteps(); ++i) {
    		MazeExplorer me = searchResult.get(i);
    		append(new MazeCell(me.getLocation()));
    	}
    }
    
    public MazeCell getStart() {return path.get(0);}
    public MazeCell getEnd() {return path.get(path.size() - 1);}
    public int getLength() {return path.size();}

    // Pre: 0 <= n < getLength()
    // Post: Returns nth point in the path
    public MazeCell getNth(int n) {return path.get(n);}
    
    public boolean hasVisited(MazeCell p) {
        return included.contains(p);
    }
    
    public void append(MazeCell next) {
        if (path.isEmpty() || next.isNeighbor(getEnd())) {
            path.add(next);
            included.add(next);
        }
    }
    
    public boolean solvesMaze(Maze target) {
        if (!getStart().equals(target.getStart()) || !getEnd().equals(target.getEnd())) {
            return false;

        }
        
        Set<MazeCell> treasureFound = new HashSet<MazeCell>();
        for (int f = 0; f < path.size() - 1; ++f) {
            MazeCell c = path.get(f);
            MazeCell n = path.get(f+1);
            if (!c.isNeighbor(n) || target.blocked(c, n)) {
                return false;
            }
            if (target.isTreasure(c)) {treasureFound.add(c);}
        }
        for (MazeCell trs: target.getTreasures()) {
            if (!treasureFound.contains(trs)) {
                return false;
            }
        }
        return true;
    }
    
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (MazeCell p: path) {
            s.append(p);
        }
        return s.toString();
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof MazePath) {
            MazePath other = (MazePath)obj;
            if (getLength() != other.getLength()) {
                return false;
            }
            for (int i = 0; i < getLength(); ++i) {
                if (!other.path.get(i).equals(path.get(i))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
    
    public int hashCode() {return toString().hashCode();}
}
