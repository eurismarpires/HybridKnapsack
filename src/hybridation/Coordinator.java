package hybridation;

import heuristics.Greedy;
import heuristics.HeuristicFactory;

import java.util.ArrayList;

import problem.Knapsack;

/**
 * Class of objects responsible of running several agents that implement different heuristics to solve the knapsack
 * problem.
 * 
 * @author omegak
 */
public class Coordinator {

	/** Number of rounds without obtaining any improvement before stopping. */
	private static final int MAX_ROUNDS_WITHOUT_IMPROVEMENT = 1000;
	
	/** Threshold for an acceptable solution. */
	private static final double WORSE_THAN_BEST_THRESHOLD = 0.5;
	
	/** The initial knapsack problem. */
	private final Knapsack initialKnapsack;
	
	/** List of agents that solves the problem. */
	private ArrayList<Agent> agents;
	
	/** The best solution found. */
	private Knapsack currentBestKnapsack;
	
	/** Counter to keep track of rounds that have produced no improvement. */
	private int roundsNotImproving = 0;
	
	/**
	 * Constructor of the class. It sets the initial knapsack problem.
	 * 
	 * @param knapsack The knapsack problem to be solved.
	 */
	public Coordinator(Knapsack knapsack) {
		initialKnapsack = new Knapsack(knapsack);
		initialize();
	}
	
	/**
	 * Returns TRUE if the coordinator has finished solving the problem, FALSE otherwise.
	 * 
	 * @return TRUE if the coordinator has finished solving the problem, FALSE otherwise.
	 */
	public boolean hasFinished() {
		return roundsNotImproving > MAX_ROUNDS_WITHOUT_IMPROVEMENT; 
	}
	
	/**
	 * Prints the state of the object.
	 */
	public void print() {
		// TODO print
	}
	
	/**
	 * Creates and runs all the agents implementing different heuristics until they find an acceptable solution. After
	 * each step, if a certain heuristic is performing extremely bad, its search space will be overridden with the
	 * current best solution found.
	 */
	public void solve() {
		
		// Creates agents
		for (HeuristicFactory heuristicType : HeuristicFactory.values()) {
			agents.add(new Agent(heuristicType.create(), currentBestKnapsack));
		}
		
		// Simulates concurrency until finished
		while (!hasFinished()) {
			runAgentsOnce();
			updateCurrentBest();
			redirectAgents();
		}
	}
	
	/**
	 * Initializes the coordinator by producing an initial best knapsack with a greedy algorithm.
	 */
	private void initialize() {
		Agent agent = new Agent(new Greedy(), initialKnapsack);
		agent.step();
		currentBestKnapsack = agent.getCurrentSolution();
	}
	
	/**
	 * Runs one step of each agent.
	 */
	private void runAgentsOnce() {
		for (Agent agent : agents) {
			agent.step();
		}
	}
	
	/**
	 * Updates current best solution and manages the counter of rounds with no improvement.
	 */
	private void updateCurrentBest() {
		boolean improved = false;
		
		// Searches for improved solutions
		for (Agent agent : agents) {
			if (agent.getCurrentSolution().evaluate() > currentBestKnapsack.evaluate()) {
				currentBestKnapsack = agent.getCurrentSolution();
				improved = true;
				roundsNotImproving = 0;
			}
		}
		
		// Increases rounds not improving
		if (!improved) {
			roundsNotImproving++;			
		}
	}
	
	/**
	 * Checks if any of the agents is performing bad enough to reset its search space to the current best solution.
	 */
	private void redirectAgents() {
		
		// Checks quality of each agent
		for (Agent agent : agents) {
			if (agent.getCurrentSolution().compareWith(currentBestKnapsack) > WORSE_THAN_BEST_THRESHOLD) {
				agent.setCurrentSolution(currentBestKnapsack);
			}
		}
	}
}
