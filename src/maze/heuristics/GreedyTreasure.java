package maze.heuristics;

import maze.core.MazeCell;
import maze.core.MazeExplorer;
import search.core.BestFirstHeuristic;

/**
 * Created by nguye on 9/1/2015.
 */
public class GreedyTreasure implements BestFirstHeuristic<MazeExplorer> {
    @Override
    public int getDistance(MazeExplorer node, MazeExplorer goal) {
        int minDistToTreasure = Integer.MAX_VALUE;
        for ( MazeCell treasure : node.getTreasureFound()){
            int distToNode = getManhantanDistance(treasure, node.getLocation());
            if (  distToNode < minDistToTreasure ){
                minDistToTreasure = distToNode;
            }
        }
        return minDistToTreasure;
    }
    public int getManhantanDistance(MazeCell first, MazeCell second){
        return Math.abs(first.X() - second.X()) + Math.abs(second.Y() - first.Y());
    }
}
