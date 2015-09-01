package maze.heuristics;

import maze.core.MazeExplorer;
import search.core.BestFirstHeuristic;

/**
 * Created by my on 8/27/15.
 */
public class ManhattanWithTiebreaker implements BestFirstHeuristic<MazeExplorer>{

    @Override
    public int getDistance(MazeExplorer node, MazeExplorer goal) {

        int dX = Math.abs(node.getLocation().X() - goal.getLocation().X());
    int dY = Math.abs(node.getLocation().Y() - goal.getLocation().Y());
    int dx = Math.abs(node.getMaze().getStart().X() - goal.getLocation().X());
    int dy =  Math.abs(node.getMaze().getStart().Y() - goal.getLocation().Y());
    int h = dX + dY;
    int cross = Math.abs(dX*dy - dx*dY);
    return (int)(h + cross * 0.001);
}
}
