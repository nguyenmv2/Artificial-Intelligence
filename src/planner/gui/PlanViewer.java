package planner.gui;

import javax.swing.*;

import planner.core.BestFirstPlanner;
import planner.core.Domain;
import planner.core.Plan;
import planner.core.PlanStep;
import planner.core.Planner;
import planner.core.Problem;
import search.core.BestFirstHeuristic;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

@SuppressWarnings("serial")
public class PlanViewer extends JFrame {
	private JComboBox<String> planners;
	private JComboBox<String> domains;
	private JComboBox<String> problems;
	private JTextArea domainText, problemText, planText;
	private JTextField numNodes, depth, bStar, solutionLength, time, timePerStep;
	private JButton makePlan, checkPlan;
	//private AIReflector<Planner> plannerMaker;
	private AIReflector<BestFirstHeuristic<PlanStep>> plannerMaker;
	
	private final String DOMAIN_BASE = "domains";
	
	public PlanViewer() throws FileNotFoundException {
		setSize(900,600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		planners = new JComboBox<>();
		domains = new JComboBox<>();
		domains.addActionListener(new DomainChanger());
		problems = new JComboBox<>();
		problems.addActionListener(new ProblemChanger());
		
		makePlan = new JButton("Create plan");
		makePlan.addActionListener(new PlanMaker());
		
		checkPlan = new JButton("Check plan");
		checkPlan.addActionListener(new PlanChecker());
		
		setLayout(new BorderLayout());
		JPanel top = new JPanel();
		top.setLayout(new GridLayout(4, 1));
		
		JPanel topControls = new JPanel();
		topControls.setLayout(new FlowLayout());
		topControls.add(new JLabel("Planners"));
		topControls.add(planners);
		topControls.add(new JLabel("Domains"));
		topControls.add(domains);
		topControls.add(new JLabel("Problems"));
		topControls.add(problems);
		
		JPanel topButtons = new JPanel();
		topButtons.setLayout(new FlowLayout());
		topButtons.add(makePlan);
		topButtons.add(checkPlan);
		
		JPanel topInfo1 = new JPanel();
		numNodes = makeInfoField("Nodes expanded", topInfo1);
		depth = makeInfoField("Maximum depth", topInfo1);
		bStar = makeInfoField("b*", topInfo1);
		solutionLength = makeInfoField("Solution length", topInfo1);
		
		JPanel topInfo2 = new JPanel();
		time = makeInfoField("Time (ms)", topInfo2);
		timePerStep = makeInfoField("ms/node", topInfo2);
		
		JPanel center = new JPanel();
		center.setLayout(new GridLayout(1, 2));
		
		JPanel inputs = new JPanel();
		inputs.setLayout(new GridLayout(2, 1));
		
		domainText = new JTextArea();
		inputs.add(createTitledScrollableArea("Domain", domainText));
		
		problemText = new JTextArea();
		inputs.add(createTitledScrollableArea("Problem", problemText));
		
		planText = new JTextArea();
		center.add(inputs);
		center.add(createTitledScrollableArea("Plan", planText));
		planText.setEditable(true);
		
		add(center, BorderLayout.CENTER);
		top.add(topControls);
		top.add(topButtons);
		top.add(topInfo1);
		top.add(topInfo2);
		add(top, BorderLayout.NORTH);
		
		populatePlanners();
		populateDomains();
	}
	
	private JTextField makeInfoField(String label, JPanel container) {
		container.add(new JLabel(label));
		JTextField result = new JTextField(6);
		result.setEditable(false);
		container.add(result);
		return result;
	}
	
	private void populatePlanners() {
		//plannerMaker = new AIReflector<Planner>(Planner.class, "planner.planners");
		plannerMaker = new AIReflector<>(BestFirstHeuristic.class, "planner.heuristics");
		for (String pStr: plannerMaker.getTypeNames()) {
			planners.addItem(pStr);
		}
	}
	
	private void populateDomains() throws FileNotFoundException {
		File domainDir = new File(DOMAIN_BASE);
		for (File domain: domainDir.listFiles()) {
			if (domain.isDirectory() && new File(domain.toString() + File.separator + "domain.pddl").exists()) {
				int start = domain.toString().lastIndexOf(File.separatorChar) + 1;
				domains.addItem(domain.toString().substring(start));
			}
		}
		
		populateProblems();
	}
	
	private void populateProblems() throws FileNotFoundException {
		refreshProblems();
		refreshDomain();
	}
	
	private File getDomainDir() {
		return new File(DOMAIN_BASE + File.separator + domains.getSelectedItem().toString());
	}
	
	private File getDomainFile() {
		return new File(getDomainDir().toString() + File.separator + "domain.pddl");
	}
	
	private File getProblemFile() {
		return new File(getDomainDir().toString() + File.separator + problems.getSelectedItem().toString());
	}
	
	private String file2string(File f) throws FileNotFoundException {
		StringBuilder sb = new StringBuilder();
		Scanner s = new Scanner(f);
		while (s.hasNextLine()) {sb.append(s.nextLine() + '\n');}
		s.close();
		return sb.toString();
	}
	
	private void refreshProblems() {
		problems.removeAllItems();
		File problemDir = getDomainDir();
		for (File problem: problemDir.listFiles()) {
			if (problem.toString().endsWith(".pddl") && !problem.toString().endsWith("domain.pddl")) {
				int start = problem.toString().lastIndexOf(File.separatorChar) + 1;
				problems.addItem(problem.toString().substring(start));
			}
		}		
	}
	
	private void refreshDomain() throws FileNotFoundException {
		domainText.setText(file2string(getDomainFile()));
	}

	private void refreshProblem() throws FileNotFoundException {
		if (problems.getSelectedIndex() > -1) {
			problemText.setText(file2string(getProblemFile()));
		}
	}
	
	private JScrollPane createTitledScrollableArea(String title, JTextArea area) {
		area.setEditable(false);
		JScrollPane jsp = new JScrollPane(area);
		jsp.setBorder(BorderFactory.createTitledBorder(title));
		return jsp;
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		new PlanViewer().setVisible(true);
	}
	
	private class DomainChanger implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				populateProblems();
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(PlanViewer.this, e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	private class ProblemChanger implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				refreshProblem();
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(PlanViewer.this, e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	private void showDouble(double d, JTextField field) {
		String display = Double.toString(d);
		int stop = Math.min(display.length(), field.getColumns());
		field.setText(display.substring(0, stop));
	}
	
	private class PlanMaker implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			for (JTextField result: new JTextField[]{numNodes, depth, bStar, solutionLength, time, timePerStep}) {
				result.setText("");
			}
			planText.setText("");
			try {
				//Planner p = plannerMaker.newInstanceOf(planners.getSelectedItem().toString());
				Planner p = new BestFirstPlanner((BestFirstHeuristic<PlanStep>)plannerMaker.newInstanceOf(planners.getSelectedItem().toString()));
				Domain d = new Domain(getDomainFile());
				Problem prob = new Problem(getProblemFile());
				long start = System.currentTimeMillis();
				Plan plan = p.makePlan(d, prob);
				long duration = System.currentTimeMillis() - start;
				if (plan.isPlanValid(prob)) {
					planText.setText(plan.toString());
					numNodes.setText(Integer.toString(p.getNumNodes()));
					depth.setText(Integer.toString(p.getMaxDepth()));
					showDouble(p.getBranchingFactor(), bStar);
					solutionLength.setText(Integer.toString(plan.length()));
					time.setText(Long.toString(duration));
					showDouble((double)duration / p.getNumNodes(), timePerStep);
				} else {
					planText.setText("No plan found");
				}
				
			} catch (InstantiationException e) {
				JOptionPane.showMessageDialog(PlanViewer.this, "Could not instantiate a " + planners.getSelectedItem());
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				JOptionPane.showMessageDialog(PlanViewer.this, "Could not access the constructor of a " + planners.getSelectedItem());
				e.printStackTrace();
			}
		}
	}
	
	private class PlanChecker implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Plan plan = Plan.buildFrom(planText.getText(), new Domain(getDomainFile()));
			JOptionPane.showMessageDialog(PlanViewer.this, plan.makeReport(new Problem(getProblemFile())));
		}
	}	
}
