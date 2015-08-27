package maze.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import search.core.BestFirstObject;

public class MazeExplorer implements BestFirstObject<MazeExplorer> {
	private Maze m;
	private MazeCell location;
	private TreeSet<MazeCell> treasureFound; 
	
	public MazeExplorer(Maze m, MazeCell location) {
		this.m = m;
		this.location = location;
		treasureFound = new TreeSet<MazeCell>();
	}
	
	public MazeCell getLocation() {return location;}



	@Override
	public ArrayList<MazeExplorer> getSuccessors() {
		ArrayList<MazeExplorer> result = new ArrayList<MazeExplorer>();
		// TODO: It should add as a successor every adjacent, unblocked neighbor square.
        ArrayList<MazeCell> neighbors = m.getNeighbors(this.getLocation());

        for ( MazeCell node : neighbors){
            if ( !m.blocked(this.getLocation(), node) && m.within(this.getLocation())) {
                MazeExplorer successor = new MazeExplorer(m, node);
                if (m.isTreasure(node)) {
                    successor.treasureFound.add(node);
                }
                successor.addTreasures(treasureFound);
                result.add(successor);
            }
        }
        return result;
	}
	 public void addTreasures(Collection<MazeCell> treasures) {
		treasureFound.addAll(treasures);
	}


	public String toString() {
		StringBuilder treasures = new StringBuilder();
		for (MazeCell t: treasureFound) {
			treasures.append(";");
			treasures.append(t.toString());
		}
		return "@" + location.toString() + treasures.toString();
	}
	
	@Override
	public int hashCode() {return toString().hashCode();}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof MazeExplorer) {
			return achieves((MazeExplorer)other);
		} else {
			return false;
		}
	}

	@Override
	public boolean achieves(MazeExplorer goal) {
		return this.location.equals(goal.location) && this.treasureFound.equals(goal.treasureFound);
	}

}
