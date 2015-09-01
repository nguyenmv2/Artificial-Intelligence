package maze.heuristics;

import maze.core.MazeCell;
import maze.core.MazeExplorer;
import search.core.BestFirstHeuristic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.SortedSet;

/**
 * Created by nguye on 9/1/2015.
 */
public class GreedyTreasure implements BestFirstHeuristic<MazeExplorer> {

    @Override
    public int getDistance(MazeExplorer node, MazeExplorer goal) {
        SortedSet<MazeCell> treasureList = node.getTreasureFound();
        if (treasureList.size() == 0 ){
            return Math.abs(node.getLocation().X() - goal.getLocation().X()) + Math.abs(node.getLocation().Y() - goal.getLocation().Y()) +
                            Math.abs(node.getLocation().X() -goal.getLocation().X()) + Math.abs(node.getLocation().Y() -goal.getLocation().Y());
        }
        ArrayList<Integer> list = new ArrayList<>();
        for (MazeCell n : treasureList){
            list.add(
                    Math.abs(n.X() - goal.getLocation().X()) + Math.abs(n.Y() - goal.getLocation().Y()) +
                            Math.abs(n.X() - node.getLocation().X()) + Math.abs(n.Y() - node.getLocation().Y())
                    );
        }
        Collections.sort(list);
        return list.get(0);
    }
}
