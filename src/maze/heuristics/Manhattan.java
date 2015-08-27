package maze.heuristics;

import maze.core.MazeExplorer;
import search.core.BestFirstHeuristic;
import search.core.BestFirstObject;

/**
 * Created by my on 8/27/15.
 */
public class Manhattan implements BestFirstHeuristic<MazeExplorer>{

    @Override
    public int getDistance(MazeExplorer node, MazeExplorer goal) {
        int dX = Math.abs(node.getLocation().X() - goal.getLocation().X());
        int dY = Math.abs(node.getLocation().Y() - goal.getLocation().Y());
        return dX + dY;
    }

}
