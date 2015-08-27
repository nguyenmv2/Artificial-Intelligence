package maze.core;

import static org.junit.Assert.*;

import org.junit.Test;

import search.core.BestFirstSearcher;

public class MazeTest {
	final static int NUM_TESTS = 100;
	final static int WIDTH = 10, HEIGHT = 15;

	@Test
	public void testNoTreasure() {
		for (int i = 0; i < NUM_TESTS; ++i) {
			Maze m = new Maze(WIDTH, HEIGHT);
			m.makeMaze(new MazeCell(0, 0), new MazeCell(WIDTH - 1, HEIGHT - 1), 0, 1);
			BestFirstSearcher<MazeExplorer> searcher = new BestFirstSearcher<>(new maze.heuristics.BreadthFirst());
			MazeExplorer endNode = new MazeExplorer(m, m.getEnd());
			searcher.solve(new MazeExplorer(m, m.getStart()), endNode);
			assertTrue(searcher.success());
			MazePath path = new MazePath(searcher, m);
			assertTrue(path.solvesMaze(m));
		}		
	}

	@Test
	public void testOneWithTreasure() {
		Maze m = new Maze(WIDTH, HEIGHT);
		m.makeMaze(new MazeCell(0, 0), new MazeCell(WIDTH - 1, HEIGHT - 1), 2, 1);
		BestFirstSearcher<MazeExplorer> searcher = new BestFirstSearcher<>(new maze.heuristics.BreadthFirst());
		MazeExplorer endNode = new MazeExplorer(m, m.getEnd());
		endNode.addTreasures(m.getTreasures());
		System.out.println(m.getTreasures());
		searcher.solve(new MazeExplorer(m, m.getStart()), endNode);
		assertTrue(searcher.success());
		MazePath path = new MazePath(searcher, m);
		assertTrue(path.solvesMaze(m));
	}
	@Test
	public void testMany() {
		for (int i = 0; i < NUM_TESTS; ++i) {
			Maze m = new Maze(WIDTH, HEIGHT);
			m.makeMaze(new MazeCell(0, 0), new MazeCell(WIDTH - 1, HEIGHT - 1), 2, 1);
			BestFirstSearcher<MazeExplorer> searcher = new BestFirstSearcher<>(new maze.heuristics.BreadthFirst());
			MazeExplorer endNode = new MazeExplorer(m, m.getEnd());
			endNode.addTreasures(m.getTreasures());
			searcher.solve(new MazeExplorer(m, m.getStart()), endNode);
			assertTrue(searcher.success());
			MazePath path = new MazePath(searcher, m);
			assertTrue(path.solvesMaze(m));
		}
	}
	
	@Test
	public void testBestFirst() {
		int totalBest = 0, totalBreadth = 0;
		for (int i = 0; i < NUM_TESTS; ++i) {
			Maze m = new Maze(WIDTH, HEIGHT);
			m.makeMaze(new MazeCell(0, 0), new MazeCell(WIDTH - 1, HEIGHT - 1), 0, 1);
			BestFirstSearcher<MazeExplorer> breadthFirst = new BestFirstSearcher<>(new maze.heuristics.BreadthFirst());
			BestFirstSearcher<MazeExplorer> bestFirst = new BestFirstSearcher<>((n, goal) -> goal.getLocation().X() - n.getLocation().X());
			MazeExplorer startNode = new MazeExplorer(m, m.getStart());
			MazeExplorer endNode = new MazeExplorer(m, m.getEnd());
			breadthFirst.solve(startNode, endNode);
			bestFirst.solve(startNode, endNode);
			assertTrue(breadthFirst.success());
			assertTrue(bestFirst.success());
			totalBest += bestFirst.getNumNodes();
			totalBreadth += breadthFirst.getNumNodes();
		}
		assertTrue(totalBest < totalBreadth);
	}
}
