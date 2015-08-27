package maze.heuristics;

import maze.core.MazeExplorer;
import search.core.BestFirstHeuristic;


public class BreadthFirst implements BestFirstHeuristic<MazeExplorer> {
    public int getDistance(MazeExplorer node, MazeExplorer goal) {
        return 0;
    }
}
