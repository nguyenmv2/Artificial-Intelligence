package maze.core;

import java.util.*;

public class Maze {
    
    private int xSize, ySize;
    private MazeCell start, end;
    
    private EnumSet<Direction>[][] barriers;
    private MazeCell[][] cells;
    private Set<MazeCell> treasures;
    
    // Pre: xSize > 0; ySize > 0
    // Post: Generates a maze in which every cell is barricaded from every
    //       other cell
    @SuppressWarnings("unchecked")
    public Maze(int xSize, int ySize) {
        this.xSize = xSize;
        this.ySize = ySize;
        start = end = null;
        cells = new MazeCell[xSize][ySize];
        barriers = new EnumSet[xSize][ySize];
        for (int x = 0; x < xSize; ++x) {
            for (int y = 0; y < ySize; ++y) {
                cells[x][y] = new MazeCell(x, y);
                barriers[x][y] = EnumSet.allOf(Direction.class);
            }
        }
        
        treasures = new LinkedHashSet<MazeCell>();
    }
    
    // Pre: 0 <= perfection <= 1.0
    // Post: Randomly generates a maze of the given size, starting at start
    //       and ending at end; if perfection = 1, the maze is perfect; if
    //       perfection = 0, the maze has very few walls
    public void makeMaze(MazeCell start, MazeCell end, int numTreasures, double perfection) {
        this.start = start;
        this.end = end;
        
        ArrayList<MazeCell> openList = new ArrayList<MazeCell>();
        Map<MazeCell,MazeCell> predecessors = new HashMap<MazeCell,MazeCell>();
        Set<MazeCell> visited = new LinkedHashSet<MazeCell>();
        openList.add(end);
        while (openList.size() > 0) {
            MazeCell current = openList.remove(openList.size() - 1);
            if (!visited.contains(current)) {
                visited.add(current);
                if (predecessors.containsKey(current)) {
                    knockDownBetween(current, predecessors.get(current));
                }
                ArrayList<MazeCell> neighbors = getNeighbors(current);
                Collections.shuffle(neighbors);
                for (MazeCell neighbor: neighbors) {
                    openList.add(neighbor);
                    predecessors.put(neighbor, current);
                }
            } else if (Math.random() > perfection) {
                if (predecessors.keySet().contains(current)) {
                    knockDownBetween(current, predecessors.get(current));
                }
            }
        }
        
        if (visited.size() != xSize * ySize) {
        	throw new IllegalStateException("Some cells weren't visited");
        }
        
        addTreasure(numTreasures);
    }

    private void addTreasure(int numTreasures) {
        treasures = new LinkedHashSet<MazeCell>();
        int numUntried = xSize * ySize - 2;
        for (int i = 0; i < xSize; ++i) {
            for (int j = 0; j < ySize; ++j) {
                MazeCell candidate = new MazeCell(i, j);
                if (!candidate.equals(getStart()) && !candidate.equals(getEnd())) {
                    double prob = (double)numTreasures / (double)numUntried;
                    if (Math.random() < prob) {
                        treasures.add(candidate);
                        numTreasures--;
                    }
                    numUntried--;
                }
            }
        }
    }
    
    public MazeCell getStart() {return start;}
    public MazeCell getEnd() {return end;}
    
    public boolean within(MazeCell mc) {
    	return mc.X() >= getXMin() && mc.X() <= getXMax() && mc.Y() >= getYMin() && mc.Y() <= getYMax();
    }
    public boolean isStart(MazeCell mc) {return start.equals(mc);}
    public boolean isEnd(MazeCell mc) {return end.equals(mc);}
    public boolean isTreasure(MazeCell mc) {return treasures.contains(mc);}
    public boolean isTreasure(int x, int y) {return isTreasure(new MazeCell(x, y));}
    
    public Set<MazeCell> getTreasures() {return Collections.unmodifiableSet(treasures);}
    
    public int getXMin() {return 0;}
    public int getYMin() {return 0;}
    public int getXMax() {return xSize - 1;}
    public int getYMax() {return ySize - 1;}
    public int getXSize() {return xSize;}
    public int getYSize() {return ySize;}
    
    // Pre: c.isNeighbor(n)
    // Post: Returns true if it is not possible to travel from c to n in one
    //       step; returns false otherwise
    public boolean blocked(MazeCell c, MazeCell n) {
        if (!c.isNeighbor(n)) {
            throw new IllegalArgumentException(c + " is not a neighbor to " + n);
        }
        
        return barriers[c.X()][c.Y()].contains(Direction.between(c, n));
    }
    
    public boolean blocked(MazeCell c, Direction d) {
    	return blocked(c.X(), c.Y(), d);
    }
    
    public boolean blocked(int x, int y, Direction d) {
    	return barriers[x][y].contains(d);
    }
    
    public String toString() {
    	StringBuilder result = new StringBuilder();
    	for (int row = 0; row < ySize; ++row) {
    		for (int col = 0; col < xSize; ++col) {
    			result.append('#');
    			appendSpot(result, blocked(col, row, Direction.N));
    		}
    		result.append("#\n");
    		
    		for (int col = 0; col < xSize; ++col) {
    			appendSpot(result, blocked(col, row, Direction.W));
    			result.append(' ');
    		}
    		appendSpot(result, blocked(xSize - 1, row, Direction.E));
    		result.append('\n');
    	}
    	
    	for (int col = 0; col < xSize; ++col) {
    		result.append('#');
    		appendSpot(result, blocked(col, ySize - 1, Direction.S));
    	}
    	result.append("#\n");
    	return result.toString();
    }
    
    private void appendSpot(StringBuilder result, boolean wall) {
    	result.append(wall ? '#' : ' ');
    }
    
    // Pre: first and second are Manhattan neighbors
    // Post: Knocks down a wall between them, if it exists
    private void knockDownBetween(MazeCell first, MazeCell second) {
    	barriers[first.X()][first.Y()].remove(Direction.between(first, second));
    	barriers[second.X()][second.Y()].remove(Direction.between(second, first));
    	
    	if (blocked(first, second) || blocked(second, first)) {
    		throw new IllegalStateException("knock down did not work");
    	}
    }
    
    // Pre: none
    // Post: Returns all legal neighbors of current in an arbitrary
    //       ordering, disregarding walls completely.
    public ArrayList<MazeCell> getNeighbors(MazeCell current) {
        ArrayList<MazeCell> neighbors = new ArrayList<MazeCell>(4);
        for (Direction d: Direction.values()) {
        	MazeCell neighbor = d.successor(current);
        	if (within(neighbor)) {
        		neighbors.add(neighbor);
        	}
        }
        return neighbors;
    }
    
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: Maze xSize ySize");
            System.exit(1);
        }
        
        int xSize = Integer.parseInt(args[0]);
        int ySize = Integer.parseInt(args[1]);
        Maze m = new Maze(xSize, ySize);
        System.out.println("Before knockdown");
        System.out.println(m);
        
        m.makeMaze(new MazeCell(m.getXMin(), m.getYMin()),
                   new MazeCell(m.getXMax(), m.getYMax()), 0, 1);
        System.out.println("Maze-ified");
        System.out.println(m);
    }
}
