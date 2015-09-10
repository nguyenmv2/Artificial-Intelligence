package planner.core;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import planner.heuristics.BreadthFirst;
import search.core.BestFirstHeuristic;

public class BreadthFirstTest {
	
	@Test
	public void test1() {
		testOne(new BreadthFirst(), "domains", "blocks", "probBLOCKS-2-0.pddl");
	}

	@Test
	public void test2() {
		testOne(new BreadthFirst(), "domains", "blocks", "probBLOCKS-4-0.pddl");
	}

	@Test
	public void test3() {
		testOne(new BreadthFirst(), "domains", "blocks", "probBLOCKS-4-1.pddl");
	}

	@Test
	public void test4() {
		testOne(new BreadthFirst(), "domains", "blocks", "probBLOCKS-4-2.pddl");
	}

	@Test
	public void test5() {
		testOne(new BreadthFirst(), "domains", "blocks", "probBLOCKS-5-0.pddl");
	}

	@Test
	public void test6() {
		testOne(new BreadthFirst(), "domains", "blocks", "probBLOCKS-5-1.pddl");
	}

	@Test
	public void test7() {
		testOne(new BreadthFirst(), "domains", "blocks", "probBLOCKS-5-2.pddl");
	}

	@Test
	public void test8() {
		testOne(new BreadthFirst(), "domains", "blocks", "probBLOCKS-6-0.pddl");
	}

	@Test
	public void test9() {
		testOne(new BreadthFirst(), "domains", "blocks", "probBLOCKS-6-1.pddl");
	}

	@Test
	public void test10() {
		testOne(new BreadthFirst(), "domains", "blocks", "probBLOCKS-6-2.pddl");
	}
	
	public static void testOne(BestFirstHeuristic<PlanStep> bfh, String... filePath) {
		String problemFile = PlanGraphTest.path2String(filePath);
		System.out.println("Trying " + problemFile);
		Domain d = new Domain(PlanGraphTest.path2String("domains", "blocks", "domain.pddl"));
		BestFirstPlanner p = new BestFirstPlanner(bfh);
		Problem prob = new Problem(new File(problemFile));
		long start = System.currentTimeMillis();
		Plan result = p.makePlan(d, prob);
		long duration = System.currentTimeMillis() - start;
		System.out.println(duration + " ms");
		System.out.println("Nodes: " + p.getNumNodes());
		System.out.println("Max Depth: " + p.getMaxDepth());
		System.out.println("b*: " + p.getBranchingFactor());
		System.out.println("length: " + result.length());
		System.out.println("result:\n" + result);
		assertTrue(result.isPlanValid(prob));
	}
}